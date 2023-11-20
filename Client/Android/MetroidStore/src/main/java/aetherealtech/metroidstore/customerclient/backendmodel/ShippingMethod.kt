package aetherealtech.metroidstore.customerclient.backendmodel

import kotlinx.serialization.Serializable

@Serializable
class ShippingMethod(
    val name: String,
    val costCents: Int
)