package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class EditPaymentMethod(
    val name: String,
    val number: String,
    val isPrimary: Boolean
)
