package com.example.metroidstore.model

data class PaymentMethodSummary(
    val id: ID,
    val name: String,
    val isPrimary: Boolean
) {
    data class ID(val value: Int)
}