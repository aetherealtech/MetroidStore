package aetherealtech.metroidstore.customerclient.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethodSummary(
    val id: Int,
    val name: String,
    val isPrimary: Boolean
)