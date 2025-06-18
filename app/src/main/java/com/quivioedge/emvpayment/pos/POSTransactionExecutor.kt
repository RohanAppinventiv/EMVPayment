package com.quivioedge.emvpayment.pos

import android.content.Context
import com.datacap.android.ProcessTransactionResponseListener
import com.quivioedge.emvpayment.pos.DsiEMVInstanceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
            dsiEMVAndroidLib.ProcessTransaction(
                requestBuilder.buildEMVSaleRequest()
            )
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