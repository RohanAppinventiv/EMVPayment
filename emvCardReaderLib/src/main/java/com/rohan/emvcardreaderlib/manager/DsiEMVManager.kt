package com.rohan.emvcardreaderlib.manager


import android.content.Context
import android.util.Log
import com.datacap.android.ProcessTransactionResponseListener
import com.rohan.emvcardreaderlib.CrState
import com.rohan.emvcardreaderlib.ErrorCode
import com.rohan.emvcardreaderlib.MessageEvent
import com.rohan.emvcardreaderlib.POSTransactionExecutor
import com.rohan.emvcardreaderlib.PRINT_TAG
import com.rohan.emvcardreaderlib.PosCardListener
import com.rohan.emvcardreaderlib.PosTransResponse
import com.rohan.emvcardreaderlib.PosTransactionListener
import com.rohan.emvcardreaderlib.PosXMLExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DsiEMVManager(val context: Context) {
    private var currentPosState: CrState = CrState.IDLE
    private var posTransactionListener: PosTransactionListener? = null
    private var posCardListener: PosCardListener? = null
    private var messageBus: MessageEvent? = null

    private val posTransactionExecutor by lazy {
        POSTransactionExecutor(context)
    }

    private val posResponseExtractor by lazy {
        PosXMLExtractor()
    }

    private val processListener = ProcessTransactionResponseListener { res ->
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(PRINT_TAG, "Process Response: $res")
            val response = posResponseExtractor.resolveResponse(res)
            when(response){
                is PosTransResponse.Error -> {
                    if(response.failureCode == ErrorCode.PSCS_ERROR.code){
                        downloadConfigParams()
                    }
                }
                is PosTransResponse.Success -> {

                }
            }
        }
    }

    private val cardDataCollectListener = ProcessTransactionResponseListener { response ->
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(PRINT_TAG, "Card Data Collect: $response")
        }
    }

    suspend fun collectCardDetails() = withContext(Dispatchers.IO) {
        posTransactionExecutor.resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "collectCardDetails called, currentPosState: $currentPosState")
            posTransactionExecutor.collectCardData()
        } else {
            Log.d(PRINT_TAG, "Cannot collect card details, transaction running")
            posTransactionListener?.onTransactionSFailed("Some other transactions running....")
        }
    }

    suspend fun downloadConfigParams() = withContext(Dispatchers.IO) {
        posTransactionExecutor.resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "downloadConfigParams called, currentPosState: $currentPosState")
            posTransactionExecutor.downloadConfig()
        } else {
            Log.d(PRINT_TAG, "Cannot call downloadConfigParams, transaction running")
            posTransactionListener?.onTransactionSFailed("Some other transactions running....")
        }
    }

    suspend fun runSaleTransaction() = withContext(Dispatchers.IO) {
        posTransactionExecutor.resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "Sale Transaction called, currentPosState: $currentPosState")
            posTransactionExecutor.doSale()
        } else {
            Log.d(PRINT_TAG, "Cannot collect card details, transaction running")
            posTransactionListener?.onTransactionSFailed("Some other transactions running....")
        }
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
