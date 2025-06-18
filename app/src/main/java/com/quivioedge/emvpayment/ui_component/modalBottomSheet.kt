package com.quivioedge.emvpayment.ui_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quivioedge.emvpayment.pos.CardBin


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetComponent(
    isSheetVisible: Boolean,
    cardBin: CardBin?,
    onDismissCallback: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    if (isSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onDismissCallback() },
            sheetState = bottomSheetState,
            containerColor = Color(0xFF1E2A38), // Solid dark sheet
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E3A4D),
                            Color(0xFF3D5A6C),
                            Color(0xFF1B2A41)
                        )
                    ),
                    alpha = 0.8f
                )
        ) {

            val fieldColors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color(0xFF90A4AE),      // Subtle blue-grey
                disabledLabelColor = Color(0xFFB0BEC5),        // Dimmed label
                disabledTextColor = Color(0xFFCFD8DC)          // Slightly dimmed text
            )

            // 🔢 Card Number
            OutlinedTextField(
                value = "XXXX-XXXX-XXXX-${cardBin?.last4digits ?: "••••"}",
                onValueChange = {},
                label = { Text("Card Number") },
                leadingIcon = {
                    Icon(Icons.Default.Menu, contentDescription = "Card Icon", tint = Color(0xFF90A4AE))
                },
                enabled = false,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🗓️ MM/YY and CVC
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = "${cardBin?.expMonth ?: "--"}/${cardBin?.expYear ?: "--"}",
                    onValueChange = {},
                    label = { Text("MM/YY") },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Expiry Icon", tint = Color(0xFF90A4AE))
                    },
                    enabled = false,
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = "***",
                    onValueChange = {},
                    label = { Text("CVC") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "CVC Icon", tint = Color(0xFF90A4AE))
                    },
                    enabled = false,
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 🔘 Close Button
            Button(
                onClick = onDismissCallback,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3D5A6C),  // Theme-consistent steel blue
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                Text("Close")
            }
        }
    }
}