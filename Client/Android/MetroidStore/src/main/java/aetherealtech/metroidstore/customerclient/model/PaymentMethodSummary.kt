package aetherealtech.metroidstore.customerclient.model

data class PaymentMethodSummary(
    val id: ID,
    val name: String,
    val isPrimary: Boolean
) {
    data class ID(val value: Int)
}