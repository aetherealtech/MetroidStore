package aetherealtech.metroidstore.customerclient.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class NewOrder(
    val addressID: Int,
    val shippingMethodName: String,
    val paymentMethodID: Int
)