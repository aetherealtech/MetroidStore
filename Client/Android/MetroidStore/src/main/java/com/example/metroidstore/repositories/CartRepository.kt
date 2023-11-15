package com.example.metroidstore.repositories

import com.example.metroidstore.datasources.CartDataSource
import com.example.metroidstore.model.ProductID

class CartRepository(
    private val dataSource: CartDataSource
) {
    suspend fun getCart() = dataSource.getCart()

    suspend fun addToCart(productID: ProductID) = dataSource.addToCart(productID)

    suspend fun removeFromCart(productID: ProductID) = dataSource.removeFromCart(productID)

    suspend fun decrementQuantity(productID: ProductID) = dataSource.decrementQuantity(productID)

    suspend fun incrementQuantity(productID: ProductID) = dataSource.incrementQuantity(productID)
}