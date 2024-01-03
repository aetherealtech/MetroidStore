package aetherealtech.metroidstore.customerclient.ui.orderitemrow

import aetherealtech.metroidstore.customerclient.model.ImageSource
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.lifecycle.ViewModel

class OrderItemRowViewModel(
    item: OrderDetails.Item,
    val select: () -> Unit,
): ViewModel() {
    val image: ImageSource
    val name: String
    val price: PriceViewModel

    init {
        image = item.image
        name = "${item.name}${if (item.quantity > 1) " (${item.quantity})" else ""}"
        price = PriceViewModel(item.price)
    }
}