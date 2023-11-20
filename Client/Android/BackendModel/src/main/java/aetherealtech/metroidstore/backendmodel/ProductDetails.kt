package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetails(
    val id: Int,
    val name: String,
    val type: String,
    val game: String,
    val images: List<String>,
    val ratings: List<Int>,
    val priceCents: Int,
)