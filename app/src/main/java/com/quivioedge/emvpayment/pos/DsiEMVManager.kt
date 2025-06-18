package com.quivioedge.emvpayment.pos

import android.content.Context
import android.util.Log
import com.datacap.android.ProcessTransactionResponseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DsiEMVManager(val context: Context) {
    private var currentPosState: PosState = PosState.Idle
    private var posTransactionListener: PosTransactionListener? = null
    private var posCardListener: PosCardListener? = null
    private var messageBus: MessageEvent? = null

    private val posTransactionExecutor by lazy {
        POSTransactionExecutor(context)
    }

    private val posResponseExtractor by lazy {
        PosXMLExtractor()
    }

    private val processListener = ProcessTransactionResponseListener { response ->
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG", "RESPONSE: $response")
            val result = posResponseExtractor.resolveResponse(response)
            when (result) {
                is PosTransResponse.Error -> {
                    when (result.failureCode) {
                        "000002" -> { //"003325" -> No xml Doc found error code
                            currentPosState = PosState.RequirePosConfig
                            runTransaction()
                        }

                        else -> {
                            currentPosState = PosState.ResetPad
                            runTransaction()
                            posTransactionListener?.onTransactionSFailed(result.msg)
                            result.printData?.let{
                                printReceipt(it)
                            }
                        }
                    }
                }

                is PosTransResponse.Success -> {
                    when (result.transactionType) {
                        TransType.EMVParamDownload -> {
                            messageBus?.onAlertReceived("Rebooting....please wait.")
                        }

                        TransType.EMVSale -> {
                            currentPosState = PosState.EmvSaleCompleted
                            runTransaction()
                        }

                        TransType.EMVPadReset -> {
                            currentPosState = PosState.Idle
                        }

                        TransType.PrintReceipt -> {
                            currentPosState = PosState.ResetPad
                            runTransaction()
                        }
                    }
                }
            }
        }
    }

    private val cardDataCollectListener = ProcessTransactionResponseListener { response ->
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG", "Card Data Collect: $response")
            val result = posResponseExtractor.resolveCardData(response)
            when (result) {
                is PosCardResponse.Error -> {
                    when (result.failureCode) {
                        "000002" -> {
                            currentPosState = PosState.RequirePosConfig
                            runTransaction()
                        }

                        else -> {
                            currentPosState = PosState.ResetPad
                            runTransaction()
                            posCardListener?.onCardFailed(result.msg)
                        }
                    }
                }

                is PosCardResponse.Success -> {
                    // Callback only for card data collected
                    currentPosState = PosState.ResetPad
                    runTransaction()
                    posCardListener?.onDataReceived(result.cardBin)
                }
            }
        }
    }

    suspend fun collectCardDetails() = withContext(Dispatchers.IO) {
        if (currentPosState == PosState.Idle) {
            posTransactionExecutor.collectCardData()
        } else {
            posTransactionListener?.onTransactionSFailed("Some other transactions running....")
        }
    }

    suspend fun runTransaction() = withContext(Dispatchers.IO) {
        when (currentPosState) {
            PosState.Idle -> {
                posTransactionExecutor.doSale()
            }

            PosState.RequirePosConfig -> {
                posTransactionExecutor.downloadParam()
            }

            PosState.EmvSaleCompleted -> {
                resetPinPad()
                posTransactionListener?.onTransactionSuccessFull("Transaction successfully completed!")
            }

            is PosState.ResetPad -> {
                resetPinPad()
            }

        }
    }

    private fun printReceipt(str: String) = runBlocking {
        posTransactionExecutor.printReceipt(str)
    }

    private fun resetPinPad() = runBlocking {
        posTransactionExecutor.resetPinPad()
    }

    fun registerTransactionListener(listener: PosTransactionListener) {
        this.posTransactionListener = listener
        posTransactionExecutor.addPosTransactionListener(processListener)
    }

    fun registerCardReaderListener(listener: PosCardListener) {
        this.posCardListener = listener
        posTransactionExecutor.addCollectCardDataListener(cardDataCollectListener)
    }

    fun registerMessageBus(listener: MessageEvent) {
        this.messageBus = listener
    }

    fun clearTransactionListener() {
        this.posTransactionListener = null
        posTransactionExecutor.clearAllListeners()
    }

}
