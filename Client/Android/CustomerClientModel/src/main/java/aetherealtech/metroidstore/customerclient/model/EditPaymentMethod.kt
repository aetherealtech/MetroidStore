package aetherealtech.metroidstore.customerclient.model

data class EditPaymentMethod(
    val name: String,
    val number: PaymentMethodDetails.Number,
    val isPrimary: Boolean
)