package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
class ShippingMethod(
    val name: String,
    val costCents: Int
)