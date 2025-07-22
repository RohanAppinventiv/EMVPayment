package com.rohan.emvcardreaderlib

sealed class CRTransactionResponse() {
    data class Success(
        val successCode: String,
        val msg: String,
        val printData: String? = null,
        val transactionDetails: SaleTransactionResponse
    ) : CRTransactionResponse()

    data class Error(
        val failureCode: String,
        val msg: String,
        val printData: String? = null
    ) : CRTransactionResponse()
}

sealed class CRPrepaidResponse() {
    data class Success(val cardBin: BIN) : CRPrepaidResponse()
    data class Error(val failureCode: String, val msg: String) : CRPrepaidResponse()
}

enum class TransType {
    EMVParamDownload,
    EMVPadReset,
    EMVSale,
    GetPrePaidStripe
}

data class BIN(
    val value: String
)

enum class ErrorCode(val code: String) {
    PSCS_ERROR("000002")
}

data class SaleTransactionResponse(
    val merchantID: String,
    val acctNo: String,
    val cardType: String,
    val tranCode: String,
    val authCode: String,
    val captureStatus: String,
    val refNo: String,
    val amount: Amount,
    val processData: String,
    val recordNo: String,
    val entryMethod: String,
    val date: String,
    val time: String,
    val applicationLabel: String,
    val payAPIId: String
)

data class Amount(
    val purchase: String,
    val gratuity: String,
    val cashBack: String,
    val authorize: String
)
