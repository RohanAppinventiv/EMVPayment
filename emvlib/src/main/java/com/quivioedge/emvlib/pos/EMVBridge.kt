package com.quivioedge.emvlib.pos

import android.content.Context

class EMVBridge (context: Context): PosCardListener, PosTransactionListener, MessageEvent {

    private val posManager by lazy {
        DsiEMVManager(context)
    }

    suspend fun startEMVTransaction(){
        posManager.runTransaction()
    }

    suspend fun collectEMVCardData(){
        posManager.collectCardDetails()
    }

    suspend fun printReceipt(template: String){
        posManager.printReceipt(template)
    }
    
    suspend fun listenTransactionResults(){
        posManager.registerTransactionListener(this)
    }

    suspend fun listenCardResults(){
        posManager.registerCardReaderListener(this)
    }

    suspend fun listenMessageBus(){
        posManager.registerMessageBus(this)
    }

    suspend fun clearAllListeners(){
        posManager.clearTransactionListener()
    }

    override fun onDataReceived(cardBin: CardBin) {
        // Return data to RN
    }

    override fun onCardFailed(message: String) {
       // Return Message to RN
    }

    override fun onTransactionSuccessFull(message: String) {
        // Return message to RN
    }

    override fun onTransactionSFailed(message: String) {
        // Return message to RN
    }

    override fun askToPrintReceipt(printBody: String) {
        // Return message to RN to show a prompt
    }

    override fun onAlertReceived(message: String) {

    }

    override fun onWarningReceived(message: String) {

    }
}