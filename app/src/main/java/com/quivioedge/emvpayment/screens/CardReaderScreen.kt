package com.quivioedge.emvpayment.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quivioedge.emvpayment.ui_component.CTAsSection
import com.quivioedge.emvpayment.ui_component.Header
import com.quivioedge.emvpayment.ui_component.ModalBottomSheetComponent
import com.quivioedge.emvpayment.ui_component.PriceLabel
import com.rohan.emvcardreaderlib.CardBin
import com.rohan.emvcardreaderlib.CardData
import com.rohan.emvcardreaderlib.EMVTransactionCommunicator
import com.rohan.emvcardreaderlib.SaleDetails
import com.rohan.emvcardreaderlib.manager.DsiEMVManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CardReaderScreen(
    modifier: Modifier = Modifier,
    cardReaderManager: DsiEMVManager,
    showSnackBar: (String) -> Unit,
    onBack: () -> Unit,
    isCardReaderConnected: Boolean,
    onConfigure: () -> Unit
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
            addCardListener(cardReaderManager, showSnackBar)
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
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Header(title = "Card Reader")
        }
        // Connection status component
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .let {
                    if (!isCardReaderConnected) it.clickable { onConfigure() } else it
                }
        ) {
            Icon(
                imageVector = if (isCardReaderConnected) Icons.Default.CheckCircle else Icons.Default.Clear,
                contentDescription = if (isCardReaderConnected) "Connected" else "Not Connected",
                tint = if (isCardReaderConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = if (isCardReaderConnected) "Connected" else "Tap to Configure",
                color = if (isCardReaderConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge
            )
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

fun addCardListener(cardReaderManager: DsiEMVManager, showSnackBar: (String) -> Unit) {
    cardReaderManager.registerListener(object: EMVTransactionCommunicator {
        override fun onError(errorMessage: String) {
            showSnackBar.invoke(errorMessage)
        }

        override fun onCardReadSuccessfully(cardData: CardData) {
            showSnackBar.invoke("Card read successfully with ${cardData.binNumber}")
        }

        override fun onSaleTransactionCompleted(saleDetails: SaleDetails) {
            showSnackBar.invoke("Transaction Completed successfully \nCard Type: ${saleDetails.cardType}")
        }

        override fun onShowMessage(message: String) {
            showSnackBar.invoke(message)
        }

    })
}


fun removeCardListener(cardReaderManager: DsiEMVManager) {
    cardReaderManager.clearTransactionListener()
}
