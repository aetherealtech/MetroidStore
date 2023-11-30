package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethodDetails(
    val id: Int,
    val name: String,
    val number: String,
    val isPrimary: Boolean
)