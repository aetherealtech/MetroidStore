package aetherealtech.metroidstore.customerclient.ui.addtocartbutton

import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.ui.addtocartconfirmation.AddToCartConfirmationViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddToCartViewModel(
    private val product: ProductDetails,
    private val cartRepository: CartRepository,
    private val viewCart: () -> Unit
): ViewModel() {
    val _confirmationViewModel = MutableSharedFlow<AddToCartConfirmationViewModel>()

    val confirmationViewModel = _confirmationViewModel
        .asSharedFlow()

    fun addToCart() {
        viewModelScope.launch {
            _confirmationViewModel.emit(
                AddToCartConfirmationViewModel(
                    product = product,
                    cartRepository = cartRepository,
                    viewCart = viewCart
                )
            )
        }
    }
}