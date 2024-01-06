package aetherealtech.metroidstore.customerclient.ui.cart

import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.itemCount
import aetherealtech.metroidstore.customerclient.model.subtotal
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.ui.cartrow.CartRowViewModel
import aetherealtech.metroidstore.customerclient.widgets.PrimaryCallToActionViewModel
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository,
    selectItem: (ProductID) -> Unit,
    proceedToCheckout: () -> Unit
): ViewModel() {
    val busy = repository.busy

    val items = repository.cart
        .mapState { cart ->
            cart
                .map { cartItem ->
                    CartRowViewModel(
                        repository = repository,
                        product = cartItem,
                        select = { selectItem(cartItem.productID) }
                    )
                }
                .toImmutableList()
        }

    val summary = CartSummaryViewModel(
        cart = repository.cart,
        proceedToCheckout = proceedToCheckout
    )

    init {
        viewModelScope.launch {
            repository.updateCart()
        }
    }
}

class CartSummaryViewModel(
    cart: StateFlow<ImmutableList<CartItem>>,
    private val proceedToCheckout: () -> Unit
): ViewModel() {
    val subtotal = cart
        .mapState { cart -> PriceViewModel(cart.subtotal) }

    val primaryAction = cart
        .mapState { cart ->
            PrimaryCallToActionViewModel(
                action = if (cart.isEmpty()) null else proceedToCheckout,
                text = "Proceed to Checkout (${cart.itemCount} items)"
            )
        }
}