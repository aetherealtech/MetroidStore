package com.example.metroidstore.model

import kotlinx.collections.immutable.ImmutableList

data class ProductDetails(
    val name: String,
    val type: String,
    val game: String,
    val images: ImmutableList<ImageSource>,
    val ratings: ImmutableList<Rating>,
    val price: Price
)