package aetherealtech.metroidstore.customerclient.model

import kotlinx.collections.immutable.ImmutableList

data class ProductSummary(
    val id: ProductID,
    val image: ImageSource,
    val name: String,
    val type: String,
    val game: String,
    val ratings: ImmutableList<Rating>,
    val price: Price
)