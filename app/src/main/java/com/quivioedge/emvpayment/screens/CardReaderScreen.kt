package com.quivioedge.emvpayment.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
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
import com.quivioedge.emvpayment.ui_component.CTAsSection
import com.quivioedge.emvpayment.ui_component.Header
import com.quivioedge.emvpayment.ui_component.ModalBottomSheetComponent
import com.quivioedge.emvpayment.ui_component.PriceLabel
import com.rohan.emvcardreaderlib.CardBin
import com.rohan.emvcardreaderlib.PosCardListener
import com.rohan.emvcardreaderlib.PosTransactionListener
import com.rohan.emvcardreaderlib.manager.DsiEMVManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CardReaderScreen(
    modifier: Modifier = Modifier,
    cardReaderManager: DsiEMVManager,
    showSnackBar: (String) -> Unit,
    onBack: () -> Unit
) {
    var bottomSheetVisibility by remember { mutableStateOf(false) }
    val cardData = remember {
        object {
            var value: CardBin? = null
        }
    }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            addCardListener(cardReaderManager)
        }

        onDispose {
            CoroutineScope(Dispatchers.Main).launch {
                removeCardListener(cardReaderManager)
            }
        }
    }

    // AddListeners logic should be called here if needed
    ModalBottomSheetComponent(
        isSheetVisible = bottomSheetVisibility,
        cardBin = com.quivioedge.emvlib.pos.CardBin(
            cardHolderName = cardData.value?.cardHolderName ?: "",
            expMonth = cardData.value?.expMonth ?: "",
            expYear = cardData.value?.expYear ?: "",
            initial6digits = cardData.value?.initial6digits ?: "",
            last4digits = cardData.value?.last4digits ?: ""
        )
    ) {
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
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(androidx.compose.ui.Alignment.TopStart)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Header(title = "Card Reader")
        }
        PriceLabel()
        CTAsSection(
            payViaCreditCardCallback = { scope.launch { cardReaderManager.runSaleTransaction() } },
            payViaInHouseCardCallback = { scope.launch { cardReaderManager.collectCardDetails() } }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
    }
}


private val posTransactionListener = object : PosTransactionListener {
    override fun onTransactionSuccessFull(message: String) {

    }

    override fun onTransactionSFailed(message: String) {
    }

    override fun askToPrintReceipt(printBody: String) {
    }

}

fun addCardListener(cardReaderManager: DsiEMVManager) {
    cardReaderManager.registerCardReaderListener(object : PosCardListener {
        override fun onDataReceived(cardBin: CardBin) {

        }

        override fun onCardFailed(message: String) {

        }
    })

    cardReaderManager.registerTransactionListener(posTransactionListener)
}


fun removeCardListener(cardReaderManager: DsiEMVManager) {
    cardReaderManager.clearTransactionListener()
}
