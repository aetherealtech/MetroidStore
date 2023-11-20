package com.example.metroidstore.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.metroidstore.model.OrderID
import com.example.metroidstore.model.OrderSummary
import com.example.metroidstore.model.Price
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.uitoolkit.PrimaryCallToAction
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import kotlinx.datetime.Clock
import java.text.DateFormat
import java.util.Date

@Composable
fun OrderSummaryRow(
    viewModel: OrderSummaryRowViewModel
) {
    Box {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = viewModel.date,
                    fontSize = 24.sp
                )
                Text(
                    text = viewModel.items,
                    fontSize = 20.sp
                )
                PriceView(
                    viewModel = viewModel.total
                )
            }

            PrimaryCallToAction(
                onClick = { viewModel.viewOrder() },
                text = "View Order"
            )
        }
        Divider()
    }
}

class OrderSummaryRowViewModel(
    private val order: OrderSummary,
    val viewOrder: (OrderID) -> Unit
): ViewModel() {
    val date: String
    val items: String
    val total: PriceViewModel

    init {
        date = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT
        ).format(Date(order.date.toEpochMilliseconds()))

        items = "${order.items} Items"

        total = PriceViewModel(order.total)
    }

    fun viewOrder() {
        viewOrder(order.id)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderSummaryRowPreview() {
    MetroidStoreTheme {
        OrderSummaryRow(
            viewModel = OrderSummaryRowViewModel(
                order = OrderSummary(OrderID(0), Clock.System.now(), 5, Price(15000)),
                viewOrder = { }
            )
        )
    }
}