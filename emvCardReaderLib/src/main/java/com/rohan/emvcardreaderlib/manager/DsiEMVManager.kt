package com.rohan.emvcardreaderlib.manager

import android.content.Context
import android.util.Log
import com.datacap.android.ProcessTransactionResponseListener
import com.rohan.emvcardreaderlib.CrState
import com.rohan.emvcardreaderlib.EMVTransactionCommunicator
import com.rohan.emvcardreaderlib.ErrorCode
import com.rohan.emvcardreaderlib.POSTransactionExecutor
import com.rohan.emvcardreaderlib.PRINT_TAG
import com.rohan.emvcardreaderlib.PosTransResponse
import com.rohan.emvcardreaderlib.PosXMLExtractor
import com.rohan.emvcardreaderlib.SaleDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DsiEMVManager(val context: Context) {
    private var currentPosState: CrState = CrState.IDLE
    private var communicator: EMVTransactionCommunicator? = null

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
                    communicator?.onError(response.msg)
                    if(response.failureCode == ErrorCode.PSCS_ERROR.code){
                        downloadConfigParams()
                    }
                }
                is PosTransResponse.Success -> {
                    // Map to SaleDetails and send callback
                    communicator?.onSaleTransactionCompleted(
                        SaleDetails(
                            accountNumber = "", // Extract from XML if available
                            status = "Success",
                            refNo = "", // Extract from XML if available
                            date = "", // Extract from XML if available
                            time = "", // Extract from XML if available
                            cardType = "" // Extract from XML if available
                        )
                    )
                }
            }
        }
    }

    suspend fun collectCardDetails() = withContext(Dispatchers.IO) {
        posTransactionExecutor.resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "collectCardDetails called, currentPosState: $currentPosState")
            posTransactionExecutor.collectCardData()
        } else {
            Log.d(PRINT_TAG, "Cannot collect card details, transaction running")
            communicator?.onError("Some other transactions running....")
        }
    }

    suspend fun downloadConfigParams() = withContext(Dispatchers.IO) {
        posTransactionExecutor.resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "downloadConfigParams called, currentPosState: $currentPosState")
            posTransactionExecutor.downloadConfig()
        } else {
            Log.d(PRINT_TAG, "Cannot call downloadConfigParams, transaction running")
            communicator?.onError("Some other transactions running....")
        }
    }

    suspend fun runSaleTransaction() = withContext(Dispatchers.IO) {
        posTransactionExecutor.resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "Sale Transaction called, currentPosState: $currentPosState")
            posTransactionExecutor.doSale()
        } else {
            Log.d(PRINT_TAG, "Cannot collect card details, transaction running")
            communicator?.onError("Some other transactions running....")
        }
    }

    fun registerListener(communicator: EMVTransactionCommunicator) {
        this.communicator = communicator
        posTransactionExecutor.addPosTransactionListener(processListener)
    }

    fun clearTransactionListener() {
        this.communicator = null
        posTransactionExecutor.clearAllListeners()
    }

}
