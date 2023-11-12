package com.example.metroidstore.model

import kotlinx.collections.immutable.ImmutableList

data class Product(
    val image: ImageSource,
    val name: String,
    val type: String,
    val game: String,
    val ratings: ImmutableList<Rating>,
    val price: Price
)