package aetherealtech.metroidstore.customerclient.ui.cartrow

import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ImageSource
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CartRowViewModel(
    private val repository: CartRepository,
    product: CartItem,
    val select: () -> Unit,
): ViewModel() {
    val id: ProductID
    val image: ImageSource
    val name: String
    val price: PriceViewModel

    val quantity = product.quantity

    init {
        id = product.productID
        image = product.image
        name = product.name
        price = PriceViewModel(product.pricePerUnit)
    }

    fun decrementQuantity() {
        viewModelScope.launch {
            repository.decrementQuantity(id)
        }
    }

    fun incrementQuantity() {
        viewModelScope.launch {
            repository.incrementQuantity(id)
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.removeFromCart(id)
        }
    }
}