package aetherealtech.metroidstore.customerclient.model

import kotlinx.collections.immutable.ImmutableList

data class ProductDetails(
    val id: ProductID,
    val name: String,
    val type: String,
    val game: String,
    val images: ImmutableList<ImageSource>,
    val ratings: ImmutableList<Rating>,
    val price: Price
)