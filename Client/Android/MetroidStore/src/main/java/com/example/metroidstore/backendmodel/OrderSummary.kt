package com.example.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class OrderSummary(
    val id: Int,
    val date: String,
    val items: Int,
    val totalCents: Int
)