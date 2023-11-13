package com.example.metroidstore.embeddedbackend

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val image: String,
    val name: String,
    val type: String,
    val game: String,
    val ratings: List<Int>,
    val priceCents: Int,
)