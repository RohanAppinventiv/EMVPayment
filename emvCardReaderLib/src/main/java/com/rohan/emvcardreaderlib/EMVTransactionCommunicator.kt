package com.rohan.emvcardreaderlib

// Interface for EMV transaction callback communication
interface EMVTransactionCommunicator {
    fun onError(errorMessage: String)
    fun onCardReadSuccessfully(cardData: CardData)
    fun onSaleTransactionCompleted(saleDetails: SaleTransactionResponse)
    fun onShowMessage(message: String)
}

// Interface for Configuration communication
interface ConfigurationCommunicator {
    fun onConfigError(errorMessage: String)
    fun onConfigPingFailed()
    fun onConfigPingSuccess()
    fun onConfigCompleted()
}

// Data class for card read result
data class CardData(
    val binNumber: String
)
