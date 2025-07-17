package com.quivioedge.emvlib.pos

import android.content.Context
import android.util.Log
import com.datacap.android.ProcessTransactionResponseListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class POSTransactionExecutor(context: Context) {

    private val dsiEMVAndroidLib  by lazy {
        DsiEMVInstanceBuilder.getInstance(context)
    }
    private val requestBuilder by lazy {
        DsiEMVRequestBuilder()
    }

    suspend fun downloadParam(){
        withContext(Dispatchers.IO){
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildEMVParamDownloadRequest()
            )
        }
    }

    suspend fun resetPinPad(){
        withContext(Dispatchers.IO){
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildPinPadResetRequest()
            )
        }
    }

    suspend fun printReceipt(data: String){
        withContext(Dispatchers.IO){
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildPrintReceiptRequest()
            )
        }
    }


    suspend fun doSale(){
        withContext(Dispatchers.IO){
            Log.d("POSTransactionExecutor", "Inside doSale()")
            val request = requestBuilder.buildEMVSaleRequest()
            Log.d("POSTransactionExecutor", "Request prepared: $request")

            dsiEMVAndroidLib.ProcessTransaction(request)
        }
    }

    suspend fun collectCardData(){
        withContext(Dispatchers.IO){
            dsiEMVAndroidLib.CollectCardData(
                requestBuilder.buildCollectCardDataResponse()
            )
        }
    }

    fun addPosTransactionListener(callback: ProcessTransactionResponseListener){
        dsiEMVAndroidLib.AddProcessTransactionResponseListener(callback)
    }

    fun addCollectCardDataListener(callback: ProcessTransactionResponseListener){
        dsiEMVAndroidLib.AddCollectCardDataResponseListener(callback)
    }

    fun clearAllListeners(){
        dsiEMVAndroidLib.ClearProcessTransactionResponseListeners()
        dsiEMVAndroidLib.ClearCollectCardDataResponseListeners()
    }

}