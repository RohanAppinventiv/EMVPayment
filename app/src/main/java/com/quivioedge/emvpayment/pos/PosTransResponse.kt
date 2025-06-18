package com.quivioedge.emvpayment.pos

sealed class PosTransResponse() {
    data class Success(val successCode: String, val msg: String,val printData: String? = null, val transactionType: TransType): PosTransResponse()
    data class Error(val failureCode: String, val msg: String,val printData: String? = null): PosTransResponse()
}

sealed class PosCardResponse(){
    data class Success(val cardBin: CardBin): PosCardResponse()
    data class Error(val failureCode: String, val msg: String): PosCardResponse()
}


data class CardBin(
    val cardHolderName: String,
    val expMonth: String,
    val expYear: String,
    val initial6digits: String,
    val last4digits: String
)


enum class TransType {
    EMVParamDownload,
    EMVPadReset,
    EMVSale,
    PrintReceipt
}