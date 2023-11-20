package aetherealtech.metroidstore.customerclient.model

data class NewOrder(
    val addressID: Address.ID,
    val shippingMethod: ShippingMethod,
    val paymentMethodID: PaymentMethodSummary.ID
)