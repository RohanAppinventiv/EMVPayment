package com.quivioedge.emvpayment.ui_component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ColumnScope.PriceLabel(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(6.5f),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Text(
            text = "$1.00",
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.LightGray,
            modifier = Modifier
                .padding(20.dp, 20.dp)
        )
    }
}