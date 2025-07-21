package com.rohan.emvcardreaderlib


class PosXMLExtractor {

    /**
     * Returns the inner text of the first occurrence of <tagName> … </tagName>
     * or null if the tag isn't present.
     */
    private fun getTag(xml: String, tagName: String): String? {
        // (?s) ⇒ dot matches newline
        val regex = Regex("(?s)<$tagName>(.*?)</$tagName>", RegexOption.IGNORE_CASE)
        return regex.find(xml)?.groupValues?.get(1)
    }

    fun resolveResponse(xml: String): CRTransactionResponse {
        return if (cmdStatus(xml) == "Error") {
            CRTransactionResponse.Error(
                getCode(xml),
                getMessage(xml),
                extractPrintDataBlock(xml),
            )
        } else {
            CRTransactionResponse.Success(
                getCode(xml),
                getMessage(xml),
                extractPrintDataBlock(xml),
                transactionDetails = mapToTranResponseData(xml)
            )
        }
    }

    fun resolvePrePaidCardData(xml: String): CRPrepaidResponse {
        return if (cmdStatus(xml) == "Success") {
            CRPrepaidResponse.Error(
                getCode(xml),
                getMessage(xml)
            )
        } else  CRPrepaidResponse.Success(
            cardBin = mapToPrepaidStripeResponse(xml)
        )
    }

    private fun extractPrintDataBlock(xml: String): String? {
        val regex = Regex("(?s)<PrintData>(.*?)</PrintData>", RegexOption.IGNORE_CASE)
        return regex.find(xml)?.value
    }

    private fun cmdStatus(xml: String) = requireNotNull(getTag(xml, "CmdStatus")) {
        "Missing <CmdStatus> in response"
    }

    private fun getCode(xml: String) = requireNotNull(getTag(xml, "DSIXReturnCode")) {
        "Missing <DSIXReturnCode> in response"
    }

    private fun getMessage(xml: String) = requireNotNull(getTag(xml, "TextResponse")) {
        "Missing <TextResponse> in response"
    }

    fun extractTranResponseData(xml: String): Map<String, String> {
        val result = mutableMapOf<String, String>()

        // Extract <TranResponse> block
        val tranResponseRegex =
            Regex("(?s)<TranResponse>(.*?)</TranResponse>", RegexOption.IGNORE_CASE)
        val tranResponseMatch =
            tranResponseRegex.find(xml)?.groupValues?.get(1) ?: return emptyMap()

        // Match all <Key>value</Key> pairs within <TranResponse>
        val keyValueRegex = Regex("<(\\w+)>(.*?)</\\1>", RegexOption.IGNORE_CASE)
        keyValueRegex.findAll(tranResponseMatch).forEach { matchResult ->
            val key = matchResult.groupValues[1]
            val value = matchResult.groupValues[2].trim()
            result[key] = value
        }

        return result
    }

    fun mapToTranResponseData(xml: String): SaleTransactionResponse {
        val map = extractTranResponseData(xml)
        return SaleTransactionResponse(
            merchantID = map["MerchantID"] ?: "",
            acctNo = map["AcctNo"] ?: "",
            cardType = map["CardType"] ?: "",
            tranCode = map["TranCode"] ?: "",
            authCode = map["AuthCode"] ?: "",
            captureStatus = map["CaptureStatus"] ?: "",
            refNo = map["RefNo"] ?: "",
            amount = Amount(
                purchase = map["Purchase"] ?: "0.00",
                gratuity = map["Gratuity"] ?: "0.00",
                cashBack = map["CashBack"] ?: "0.00",
                authorize = map["Authorize"] ?: "0.00"
            ),
            processData = map["ProcessData"] ?: "",
            recordNo = map["RecordNo"] ?: "",
            entryMethod = map["EntryMethod"] ?: "",
            date = map["Date"] ?: "",
            time = map["Time"] ?: "",
            applicationLabel = map["ApplicationLabel"] ?: "",
            payAPIId = map["PayAPI_Id"] ?: ""
        )
    }

    fun extractAllPrepaidCardKeyValues(xml: String): Map<String, String> {
        val result = mutableMapOf<String, String>()

        // Extract <RStream> content
        val rStreamRegex = Regex("(?s)<RStream>(.*?)</RStream>", RegexOption.IGNORE_CASE)
        val rStreamContent = rStreamRegex.find(xml)?.groupValues?.get(1) ?: return emptyMap()

        // Match all <Key>value</Key> pairs under RStream (excluding nested ones)
        val keyValueRegex = Regex("<(\\w+)>([^<]*)</\\1>", RegexOption.IGNORE_CASE)
        keyValueRegex.findAll(rStreamContent).forEach { matchResult ->
            val key = matchResult.groupValues[1]
            val value = matchResult.groupValues[2].trim()
            result[key] = value
        }

        return result
    }

    fun mapToPrepaidStripeResponse(xml: String): BIN{
        val map = extractAllPrepaidCardKeyValues(xml)
        return BIN (value = map["PrePaidTrack2"] ?: "0123456789")
    }
}