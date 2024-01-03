package aetherealtech.metroidstore.customerclient.ui.ordersummaryrow

import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import aetherealtech.metroidstore.customerclient.utilities.displayString
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.lifecycle.ViewModel

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