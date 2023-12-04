package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class ProductSummary(
    val id: Int,
    val image: String,
    val name: String,
    val type: String,
    val game: String,
    val priceCents: Int,
    val ratingCount: Int,
    val rating: Float?
)