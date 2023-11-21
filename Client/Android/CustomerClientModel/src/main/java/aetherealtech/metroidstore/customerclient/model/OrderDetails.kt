package aetherealtech.metroidstore.customerclient.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant

data class OrderDetails(
    val date: Instant,
    val address: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val total: Price,
    val latestStatus: OrderStatus,
    val items: ImmutableList<Item>
) {
    data class Item(
        val productID: ProductID,
        val name: String,
        val image: ImageSource,
        val quantity: Int,
        val price: Price
    )
}