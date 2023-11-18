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

val List<CartItem>.itemCount
    get() = this
        .map { item -> item.quantity }
        .fold(0) { lhs, rhs -> lhs + rhs }

val List<CartItem>.subtotal
    get() = this
        .map { item -> item.price }
        .fold(Price.zero) { lhs, rhs -> lhs + rhs }