package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetails(
    val date: String,
    val address: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val totalCents: Int,
    val latestStatus: String,
    val items: List<Item>
) {
    @Serializable
    data class Item(
        val productID: Int,
        val name: String,
        val image: String,
        val quantity: Int,
        val priceCents: Int
    )
}