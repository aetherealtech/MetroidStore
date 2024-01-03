package aetherealtech.metroidstore.customerclient.datasources.api

import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ProductID
import kotlinx.collections.immutable.ImmutableList

interface CartDataSource {
    suspend fun getCart(): ImmutableList<CartItem>
    suspend fun addToCart(productID: ProductID): ImmutableList<CartItem>
    suspend fun removeFromCart(productID: ProductID): ImmutableList<CartItem>

    suspend fun incrementQuantity(productID: ProductID): ImmutableList<CartItem>
    suspend fun decrementQuantity(productID: ProductID): ImmutableList<CartItem>
}