package com.quivioedge.emvpayment.pos

class PosXMLExtractor {

    /**
     * Returns the inner text of the first occurrence of <tagName> … </tagName>
     * or null if the tag isn’t present.
     */
    private fun getTag(xml: String, tagName: String): String? {
        // (?s) ⇒ dot matches newline
        val regex = Regex("(?s)<$tagName>(.*?)</$tagName>", RegexOption.IGNORE_CASE)
        return regex.find(xml)?.groupValues?.get(1)
    }

    fun resolveResponse(xml: String): PosTransResponse {
        return if (cmdStatus(xml) == "Success") {
            PosTransResponse.Success(
                getCode(xml),
                getMessage(xml),
                extractPrintDataBlock(xml),
                tranCode(xml),
            )
        } else PosTransResponse.Error(
            getCode(xml),
            getMessage(xml),
            extractPrintDataBlock(xml),
            )
    }

    fun resolveCardData(xml: String): PosCardResponse {
        return if (cmdStatus(xml) == "Success") {
            PosCardResponse.Success(
                CardBin(
                    cardHolderName = cardHolderName(xml) ?: "",
                    expYear = cardExpYear(xml),
                    expMonth = cardExpMonth(xml),
                    initial6digits = cardInitial6(xml),
                    last4digits = cardLast4(xml)
                )
            )
        } else PosCardResponse.Error(
            getCode(xml),
            getMessage(xml)
        )
    }

    private fun extractPrintDataBlock(xml: String): String? {
        val regex = Regex("(?s)<PrintData>(.*?)</PrintData>", RegexOption.IGNORE_CASE)
        return regex.find(xml)?.value
    }

    private fun cardHolderName(xml: String) =
        getTag(xml, "CardholderName")  // Some Cards don't fetch card holder's Name

    private fun cardExpMonth(xml: String) = requireNotNull(getTag(xml, "ExpDateMonth")) {
        "Missing <ExpDateMonth> in response"
    }

    private fun cardExpYear(xml: String) = requireNotNull(getTag(xml, "ExpDateYear")) {
        "Missing <ExpDateYear> in response"
    }

    private fun cardInitial6(xml: String) = requireNotNull(getTag(xml, "CardBin")) {
        "Missing <CardBin> in response"
    }

    private fun cardLast4(xml: String) = requireNotNull(getTag(xml, "Last4")) {
        "Missing <Last4> in response"
    }

    private fun cmdStatus(xml: String) = requireNotNull(getTag(xml, "CmdStatus")) {
        "Missing <CmdStatus> in response"
    }

    private fun tranCode(xml: String) =
        TransType.valueOf(
            getTag(xml, "TranCode") ?: TransType.EMVPadReset.name
        )

    private fun getCode(xml: String) = requireNotNull(getTag(xml, "DSIXReturnCode")) {
        "Missing <DSIXReturnCode> in response"
    }

    private fun getMessage(xml: String) = requireNotNull(getTag(xml, "TextResponse")) {
        "Missing <TextResponse> in response"
    }
}