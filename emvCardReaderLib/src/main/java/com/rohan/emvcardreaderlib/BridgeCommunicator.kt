package com.rohan.emvcardreaderlib

interface BridgeCommunicator {
    fun onError(errorMessage: String)
    fun onCardReadSuccessfully(cardData: CardData)
    fun onSaleTransactionCompleted(saleDetails: SaleTransactionResponse)
    fun onShowMessage(message: String)
    fun onConfigError(errorMessage: String)
    fun onConfigPingFailed()
    fun onConfigPingSuccess()
    fun onConfigCompleted()
}