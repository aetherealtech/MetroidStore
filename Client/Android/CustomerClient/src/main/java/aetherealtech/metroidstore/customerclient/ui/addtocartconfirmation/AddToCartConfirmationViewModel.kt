package aetherealtech.metroidstore.customerclient.ui.addtocartconfirmation

import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.uitoolkit.ConfirmationModalViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddToCartConfirmationViewModel(
    private val _addedViewModel: MutableStateFlow<AddedToCartViewModel?>
): ConfirmationModalViewModel<AddedToCartViewModel>(_addedViewModel.asStateFlow()) {
    constructor(
        product: ProductDetails,
        cartRepository: CartRepository,
        viewCart: () -> Unit
    ) : this(MutableStateFlow(null)) {
        viewModelScope.launch {
            cartRepository.addToCart(product.id)

            _addedViewModel.value = AddedToCartViewModel(
                product = product,
                viewCart = viewCart
            )
        }
    }
}