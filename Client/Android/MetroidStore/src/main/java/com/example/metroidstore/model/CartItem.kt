package com.example.metroidstore.model

data class CartItem(
    val productID: ProductID,
    val name: String,
    val image: ImageSource,
    val price: Price,
    val quantity: Int
)