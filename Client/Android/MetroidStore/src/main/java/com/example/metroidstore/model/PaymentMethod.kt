package com.example.metroidstore.model

data class PaymentMethod(
    val id: ID,
    val name: String
) {
    data class ID(val value: Int)
}