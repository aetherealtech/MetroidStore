package com.example.metroidstore.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.metroidstore.model.Price
import java.math.BigDecimal
import java.text.DecimalFormat

@Composable
fun PriceView(
    viewModel: PriceViewModel
) {
    Text(
        text = viewModel.price,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
}

class PriceViewModel(
    price: Price
): ViewModel() {
    private val dollarsFormat = DecimalFormat("0")
    private val centsFormat = DecimalFormat("00")

    val price: String

    init {
        val dollars = price.cents / 100
        val cents = price.cents % 100

        this.price = "$${dollarsFormat.format(dollars)}.${centsFormat.format(cents)}"
    }
}