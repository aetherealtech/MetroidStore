package com.example.metroidstore.datasources

import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.ProductID
import kotlinx.collections.immutable.ImmutableList

interface CartDataSource {
    suspend fun getCart(): ImmutableList<CartItem>
    suspend fun addToCart(productID: ProductID): ImmutableList<CartItem>
    suspend fun removeFromCart(productID: ProductID): ImmutableList<CartItem>

    suspend fun incrementQuantity(productID: ProductID): ImmutableList<CartItem>
    suspend fun decrementQuantity(productID: ProductID): ImmutableList<CartItem>
}