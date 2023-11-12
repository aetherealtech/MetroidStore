package com.example.metroidstore.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.text.DecimalFormat

@Composable
fun PriceView(
    price: BigDecimal
) {
    val format = DecimalFormat("#,###.00")

    Text(
        text = "$${format.format(price)}",
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
}