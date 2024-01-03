package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.api.CartDataSource
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ProductID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository(
    private val dataSource: CartDataSource
) {
    private val _cart: MutableStateFlow<ImmutableList<CartItem>> = MutableStateFlow(persistentListOf())
    private val _busy = MutableStateFlow(false)

    val cart = _cart
        .asStateFlow()

    val busy = _busy
        .asStateFlow()
    suspend fun updateCart() {
        _cart.value = dataSource.getCart()
    }

    suspend fun addToCart(productID: ProductID) {
        update { dataSource.addToCart(productID) }
    }

    suspend fun removeFromCart(productID: ProductID) {
        update { dataSource.removeFromCart(productID) }
    }

    suspend fun decrementQuantity(productID: ProductID) {
        update { dataSource.decrementQuantity(productID) }
    }

    suspend fun incrementQuantity(productID: ProductID) {
        update { dataSource.incrementQuantity(productID) }
    }

    private suspend fun update(
        action: suspend () -> ImmutableList<CartItem>
    ) {
        _busy.value = true
        _cart.value = action()
        _busy.value = false
    }
}