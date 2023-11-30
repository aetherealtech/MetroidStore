package aetherealtech.metroidstore.customerclient.model

data class PaymentMethodID(val value: Int)

data class PaymentMethodSummary(
    val id: PaymentMethodID,
    val name: String,
    val isPrimary: Boolean
)

data class PaymentMethodDetails(
    val id: PaymentMethodID,
    val name: String,
    val number: Number,
    val isPrimary: Boolean
) {
    data class Number(val value: String)
}