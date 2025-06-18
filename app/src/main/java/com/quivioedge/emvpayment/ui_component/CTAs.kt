package com.quivioedge.emvpayment.ui_component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColumnScope.CTAsSection(
    payViaCreditCardCallback: () -> Unit,
    payViaInHouseCardCallback: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .weight(3f)
    ){
        ElevatedButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(8.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color(0xFF003366),
                contentColor = Color.White
            ),
            onClick = payViaCreditCardCallback
        ) {
            Text(
                "Pay with Credit Card",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center
            )
        }

        ElevatedButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(8.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color(0xFF5C677D),
                contentColor = Color.White
            ),
            shape = RectangleShape,
            onClick = payViaInHouseCardCallback
        ) {
            Text(
                "Pay with In-house Card",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center

            )
        }
    }
}
