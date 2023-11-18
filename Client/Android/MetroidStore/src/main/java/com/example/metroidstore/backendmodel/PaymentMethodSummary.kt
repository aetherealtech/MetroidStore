package com.example.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethodSummary(
    val id: Int,
    val name: String,
    val isPrimary: Boolean
)