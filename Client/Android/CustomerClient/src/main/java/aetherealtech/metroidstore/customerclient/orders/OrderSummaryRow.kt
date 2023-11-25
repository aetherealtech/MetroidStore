package aetherealtech.metroidstore.customerclient.orders

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
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import aetherealtech.metroidstore.customerclient.utilities.displayString
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.text.font.FontWeight
import kotlinx.datetime.Clock
import java.text.DateFormat
import java.util.Date

@Composable
fun OrderSummaryRow(
    viewModel: OrderSummaryRowViewModel
) {
    Box {
        Column {
            Text(
                text = viewModel.date,
                fontSize = 24.sp
            )
            Text(
                text = viewModel.status,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.items,
                        fontSize = 20.sp
                    )
                    PriceView(
                        viewModel = viewModel.total
                    )
                }

                Spacer(modifier = Modifier.weight(1.0f))

                PrimaryCallToAction(
                    modifier = Modifier.width(128.dp),
                    onClick = { viewModel.viewOrder() },
                    text = "View Order"
                )
            }
        }
        Divider()
    }
}

class OrderSummaryRowViewModel(
    private val order: OrderSummary,
    val viewOrder: (OrderID) -> Unit
): ViewModel() {
    val date: String
    val status: String
    val items: String
    val total: PriceViewModel

    init {
        date = order.date.displayString
        status = order.latestStatus.value

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
                order = OrderSummary(OrderID(0), Clock.System.now(), 5, Price(15000), OrderStatus.SHIPPED),
                viewOrder = { }
            )
        )
    }
}