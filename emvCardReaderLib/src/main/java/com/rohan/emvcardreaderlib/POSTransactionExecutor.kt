package com.rohan.emvcardreaderlib

import android.content.Context
import android.util.Log
import com.datacap.android.ProcessTransactionResponseListener
import com.rohan.emvcardreaderlib.builder.DsiEMVInstanceBuilder
import com.rohan.emvcardreaderlib.builder.DsiEMVRequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class POSTransactionExecutor(context: Context) {

    private val dsiEMVAndroidLib  by lazy {
        DsiEMVInstanceBuilder.getInstance(context)
    }
    private val requestBuilder by lazy {
        DsiEMVRequestBuilder()
    }

    suspend fun doSale(amount: String) {
        withContext(Dispatchers.IO){
            val request = requestBuilder.buildEMVSaleRequest(amount)
            Log.d(PRINT_TAG, "Sale request prepared: $request")
            dsiEMVAndroidLib.ProcessTransaction(request)
        }
    }

    suspend fun collectCardData(){
        withContext(Dispatchers.IO){
            Log.d(PRINT_TAG, "Inside CollectCardData()")
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildCollectCardDataRequest()
            )
        }
    }

    suspend fun downloadConfig(){
        withContext(Dispatchers.IO){
            Log.d(PRINT_TAG, "Inside downloadConfig()")
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildEMVParamDownloadRequest()
            )
        }
    }

    suspend fun resetPinPad(){
        withContext(Dispatchers.IO){
            Log.d(PRINT_TAG, "Inside Reset Pin()")
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildPinPadResetRequest()
            )
        }
    }

    fun addPosTransactionListener(callback: ProcessTransactionResponseListener){
        dsiEMVAndroidLib.AddProcessTransactionResponseListener(callback)
    }

    fun clearAllListeners(){
        dsiEMVAndroidLib.ClearProcessTransactionResponseListeners()
        dsiEMVAndroidLib.ClearCollectCardDataResponseListeners()
    }

}


