package com.example.metroidstore.model

data class CartItem(
    val productID: ProductID,
    val name: String,
    val image: ImageSource,
    val pricePerUnit: Price,
    val quantity: Int
) {
    val price = pricePerUnit * quantity
}