package com.quivioedge.emvpayment.pos


interface PosTransactionListener {
    fun onTransactionSuccessFull(message: String)
    fun onTransactionSFailed(message: String)
    fun askToPrintReceipt(printBody: String)
}

interface PosCardListener {
    fun onDataReceived(cardBin: CardBin)
    fun onCardFailed(message: String)
}

interface MessageEvent {
    fun onAlertReceived(message: String)
    fun onWarningReceived(message: String)
}