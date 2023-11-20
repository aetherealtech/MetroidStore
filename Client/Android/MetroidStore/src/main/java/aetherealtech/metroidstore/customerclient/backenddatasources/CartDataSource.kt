package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.CartDataSource
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ProductID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class CartDataSourceBackend(
    private val client: BackendClient
): CartDataSource {
    override suspend fun getCart(): ImmutableList<CartItem> = client.getCart().toImmutableList()

    override suspend fun addToCart(productID: ProductID): ImmutableList<CartItem> = client.addToCart(productID).toImmutableList()

    override suspend fun removeFromCart(productID: ProductID): ImmutableList<CartItem> = client.removeFromCart(productID).toImmutableList()

    override suspend fun decrementQuantity(productID: ProductID): ImmutableList<CartItem> = client.decrementQuantity(productID).toImmutableList()

    override suspend fun incrementQuantity(productID: ProductID): ImmutableList<CartItem> = client.incrementQuantity(productID).toImmutableList()
}