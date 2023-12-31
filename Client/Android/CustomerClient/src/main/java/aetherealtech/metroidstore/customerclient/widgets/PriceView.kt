package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import aetherealtech.metroidstore.customerclient.model.Price
import java.text.DecimalFormat

@Composable
fun PriceView(
    viewModel: PriceViewModel,
    size: TextUnit = 24.sp,
) {
    Text(
        text = viewModel.price,
        fontWeight = FontWeight.Bold,
        fontSize = size
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