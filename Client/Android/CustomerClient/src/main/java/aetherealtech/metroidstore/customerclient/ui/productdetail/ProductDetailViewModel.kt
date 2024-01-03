package aetherealtech.metroidstore.customerclient.ui.productdetail

import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.ui.addtocartbutton.AddToCartViewModel
import aetherealtech.metroidstore.customerclient.utilities.parallelMap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    productID: ProductID,
    repository: ProductRepository,
    private val viewCart: () -> Unit
): ViewModel() {
    private val _name = MutableStateFlow<String?>(null)
    private val _images = MutableStateFlow<ImmutableList<ImageBitmap>?>(null)

    private val _addToCartViewModel = MutableStateFlow<AddToCartViewModel?>(null)

    val name = _name
        .asStateFlow()

    val images = _images
        .asStateFlow()

    val addToCartViewModel = _addToCartViewModel
        .asStateFlow()

    init {
        viewModelScope.launch {
            val product = repository.getProductDetails(productID)

            _name.value = product.name

            _images.value = product.images
                .parallelMap { imageSource -> imageSource.load().asImageBitmap() }
                .toImmutableList()

            _addToCartViewModel.value = AddToCartViewModel(
                product = product,
                cartRepository = repository.cart,
                viewCart = viewCart
            )
        }
    }
}