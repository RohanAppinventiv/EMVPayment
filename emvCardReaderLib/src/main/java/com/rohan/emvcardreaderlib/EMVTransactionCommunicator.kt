package com.rohan.emvcardreaderlib

// Interface for EMV transaction callback communication
interface EMVTransactionCommunicator {
    fun onError(errorMessage: String)
    fun onCardReadSuccessfully(cardData: CardData)
    fun onSaleTransactionCompleted(saleDetails: SaleDetails)
    fun onShowMessage(message: String)
}

// Data class for card read result
data class CardData(
    val binNumber: String
)

// Data class for sale transaction result
data class SaleDetails(
    val accountNumber: String,
    val status: String,
    val refNo: String,
    val date: String,
    val time: String,
    val cardType: String
) 