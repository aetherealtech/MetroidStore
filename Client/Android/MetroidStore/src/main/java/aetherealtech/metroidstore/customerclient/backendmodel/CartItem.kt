package aetherealtech.metroidstore.customerclient.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productID: Int,
    val name: String,
    val image: String,
    val priceCents: Int,
    val quantity: Int
)