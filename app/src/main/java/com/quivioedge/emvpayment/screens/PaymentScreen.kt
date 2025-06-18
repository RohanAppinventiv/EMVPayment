package com.quivioedge.emvpayment.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.quivioedge.emvpayment.pos.CardBin
import com.quivioedge.emvpayment.pos.DsiEMVManager
import com.quivioedge.emvpayment.pos.MessageEvent
import com.quivioedge.emvpayment.pos.PosCardListener
import com.quivioedge.emvpayment.pos.PosTransactionListener
import com.quivioedge.emvpayment.ui_component.CTAsSection
import com.quivioedge.emvpayment.ui_component.Header
import com.quivioedge.emvpayment.ui_component.ModalBottomSheetComponent
import com.quivioedge.emvpayment.ui_component.PriceLabel
import kotlinx.coroutines.launch


@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    posManager: DsiEMVManager,
    showSnackBar: (String) -> Unit
) {

    var bottomSheetVisibility by remember { mutableStateOf(false) }
    val cardData = remember {
        object {
            var value: CardBin? = null
        }
    }

    val scope = rememberCoroutineScope()

    AddListeners(
        posManager = posManager,
        showSnackBar = showSnackBar,
        cardDataReceived = { data ->
            cardData.value = data
            bottomSheetVisibility = true
        }
    )

    ModalBottomSheetComponent(bottomSheetVisibility, cardData.value) {
        bottomSheetVisibility = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3A4D),
                        Color(0xFF3D5A6C),
                        Color(0xFF1B2A41)
                    )
                )
            )
            .then(modifier)
    ) {
        Header()

        PriceLabel()

        CTAsSection(
           payViaCreditCardCallback = {scope.launch { posManager.runTransaction() }} ,
            payViaInHouseCardCallback = {scope.launch { posManager.collectCardDetails() }}
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
    }
}


@Composable
fun AddListeners(
    posManager: DsiEMVManager,
    showSnackBar: (String) -> Unit,
    cardDataReceived: (CardBin) -> Unit
) {
    DisposableEffect(Unit) {
        val posTransactionalListener = object : PosTransactionListener {
            override fun onTransactionSuccessFull(message: String) {
                showSnackBar.invoke(message)
            }

            override fun onTransactionSFailed(message: String) {
                showSnackBar.invoke(message)
            }

            override fun askToPrintReceipt(printBody: String) {

            }

        }

        val posCardReaderListener = object : PosCardListener {
            override fun onDataReceived(cardBin: CardBin) {
                cardDataReceived.invoke(cardBin)
            }

            override fun onCardFailed(message: String) {
                showSnackBar.invoke(message)
            }

        }

        val messageBus = object : MessageEvent {
            override fun onAlertReceived(message: String) {
                showSnackBar.invoke(message)
            }

            override fun onWarningReceived(message: String) {
                showSnackBar.invoke(message)
            }
        }

        posManager.registerMessageBus(messageBus)
        posManager.registerCardReaderListener(posCardReaderListener)
        posManager.registerTransactionListener(posTransactionalListener)

        onDispose {
            posManager.clearTransactionListener()
        }
    }
}