package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.api.CartDataSource
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ProductID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

class CartDataSourceFake(
    private val products: List<DataSourceFake.Product>
): CartDataSource {
    data class CartItem(
        val productID: ProductID,
        val quantity: Int = 1
    ) {
        fun decremented(): CartItem {
            return CartItem(productID, quantity - 1)
        }

        fun incremented(): CartItem {
            return CartItem(productID, quantity + 1)
        }
    }

    var cart = persistentListOf<CartItem>()

    override suspend fun getCart(): ImmutableList<aetherealtech.metroidstore.customerclient.model.CartItem> {
        return cart
            .map { item -> products.cartItem(item) }
            .toImmutableList()
    }

    override suspend fun addToCart(productID: ProductID): ImmutableList<aetherealtech.metroidstore.customerclient.model.CartItem> {
        if(cart.find { cartItem -> cartItem.productID == productID } != null) {
            incrementQuantity(productID)
        } else {
            cart = cart
                .add(CartItem(productID))
        }

        return getCart()
    }

    override suspend fun removeFromCart(productID: ProductID): ImmutableList<aetherealtech.metroidstore.customerclient.model.CartItem> {
        cart = cart
            .filter { cartItem -> cartItem.productID != productID }
            .toPersistentList()

        return getCart()
    }

    override suspend fun decrementQuantity(productID: ProductID): ImmutableList<aetherealtech.metroidstore.customerclient.model.CartItem> {
        cart = cart
            .map { cartItem ->
                if(cartItem.productID == productID) cartItem.decremented()
                else cartItem
            }
            .toPersistentList()

        return getCart()
    }

    override suspend fun incrementQuantity(productID: ProductID): ImmutableList<aetherealtech.metroidstore.customerclient.model.CartItem> {
        cart = cart
            .map { cartItem ->
                if(cartItem.productID == productID) cartItem.incremented()
                else cartItem
            }
            .toPersistentList()

        return getCart()
    }
}

fun List<DataSourceFake.Product>.cartItem(
    item: CartDataSourceFake.CartItem
): CartItem {
    val product = this
        .find { product -> product.id == item.productID }!!

    return product.cartItem(
        item.quantity
    )
}

fun DataSourceFake.Product.cartItem(
    quantity: Int
): CartItem {
    return CartItem(
        productID = id,
        name = name,
        image = images[0],
        pricePerUnit = price,
        quantity = quantity
    )
}