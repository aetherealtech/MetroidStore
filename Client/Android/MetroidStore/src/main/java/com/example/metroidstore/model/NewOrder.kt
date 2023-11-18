package com.example.metroidstore.model

data class NewOrder(
    val addressID: Address.ID,
    val shippingMethod: ShippingMethod,
    val paymentMethodID: PaymentMethodSummary.ID
)