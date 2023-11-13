package com.example.metroidstore.embeddedbackend

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetails(
    val name: String,
    val type: String,
    val game: String,
    val images: List<String>,
    val ratings: List<Int>,
    val priceCents: Int,
)