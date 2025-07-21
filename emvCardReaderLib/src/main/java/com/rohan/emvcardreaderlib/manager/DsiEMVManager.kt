package com.rohan.emvcardreaderlib.manager

import android.content.Context
import android.util.Log
import com.datacap.android.ProcessTransactionResponseListener
import com.rohan.emvcardreaderlib.CardData
import com.rohan.emvcardreaderlib.ConfigurationCommunicator
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
    private var configCommunicator: ConfigurationCommunicator? = null

    private val posTransactionExecutor by lazy {
        POSTransactionExecutor(context)
    }

    private val posResponseExtractor by lazy {
        PosXMLExtractor()
    }


    private suspend fun resetPinPad() {
        currentPosState = CrState.Reset
        posTransactionExecutor.resetPinPad()
    }

    suspend fun checkConfig() {
        currentPosState = CrState.RunConfig
        posTransactionExecutor.resetPinPad()
    }

    suspend fun pingConfig() {
        currentPosState = CrState.PingConfig
        posTransactionExecutor.resetPinPad()
    }

    suspend fun collectCardDetails() = withContext(Dispatchers.IO) {
        resetPinPad()
        if (currentPosState == CrState.IDLE) {
            Log.d(PRINT_TAG, "collectCardDetails called, currentPosState: $currentPosState")
            posTransactionExecutor.collectCardData()
        } else {
            Log.d(PRINT_TAG, "Cannot collect card details, transaction running")
            communicator?.onError("Some other transactions running....")
        }
    }

    private suspend fun downloadConfigParams() = withContext(Dispatchers.IO) {
        Log.d(PRINT_TAG, "downloadConfigParams called, currentPosState: $currentPosState")
        posTransactionExecutor.downloadConfig()
    }

    suspend fun runSaleTransaction() = withContext(Dispatchers.IO) {
        resetPinPad()
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

    private val processListener = ProcessTransactionResponseListener { res ->
        Log.d(PRINT_TAG, "Process Response: $res")
        CoroutineScope(Dispatchers.IO).launch {
            val response = posResponseExtractor.resolveResponse(res)
            when (response) {
                is PosTransResponse.Error -> {
                    checkErrorResponse(response)
                }

                is PosTransResponse.Success -> {
                    checkSuccessResponse(response)
                }
            }
        }
    }

    private suspend fun checkErrorResponse(error: PosTransResponse.Error) {
        when (currentPosState) {
            CrState.RunConfig -> {
                if (error.failureCode == ErrorCode.PSCS_ERROR.code) {
                    downloadConfigParams()
                } else {
                    configCommunicator?.onConfigError(error.msg)
                }
            }

            CrState.PingConfig -> configCommunicator?.onConfigPingFailed()
            else -> {
                communicator?.onError(error.msg)
            }

        }
        currentPosState = CrState.IDLE
    }

    private fun checkSuccessResponse(success: PosTransResponse.Success) {
        when (currentPosState) {
            CrState.PingConfig -> {
                configCommunicator?.onConfigPingSuccess()
            }

            CrState.RunConfig -> {
                configCommunicator?.onConfigCompleted()
            }

            CrState.EmvSale -> {
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

            CrState.PrePaidCardDataCollect -> {
                communicator?.onCardReadSuccessfully(
                    CardData(
                        binNumber = ""
                    )
                )
            }

            else -> Unit
        }

        // Reset The state to IDLE
        currentPosState = CrState.IDLE
    }

}
