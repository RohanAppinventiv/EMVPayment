package com.rohan.emvcardreaderlib


interface PosTransactionListener {
    fun onTransactionSuccessFull(message: String)
    fun onTransactionSFailed(message: String)
    fun askToPrintReceipt(printBody: String)
}

interface PosCardListener {
    fun onDataReceived(cardBin: CardBin)
    fun onCardFailed(message: String)
}
