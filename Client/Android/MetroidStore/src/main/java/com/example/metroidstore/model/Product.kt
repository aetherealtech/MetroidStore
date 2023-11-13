package com.example.metroidstore.model

import kotlinx.collections.immutable.ImmutableList

data class Product(
    val id: ID,
    val image: ImageSource,
    val name: String,
    val type: String,
    val game: String,
    val ratings: ImmutableList<Rating>,
    val price: Price
) {
    data class ID(val value: Int)
}