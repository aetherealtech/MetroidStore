package aetherealtech.metroidstore.customerclient.ui.productlist

import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.ui.productlistrow.ProductRowViewModel
import aetherealtech.kotlinflowsextensions.cacheLatest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModel(
    productRepository: ProductRepository,
    selectProduct: (ProductID) -> Unit
): ViewModel() {
    val items: StateFlow<ImmutableList<ProductRowViewModel>?>

    val searchQuery = MutableStateFlow<String?>(null)

    init {
        items = searchQuery
            .flatMapLatest { searchQuery ->
                val result = MutableStateFlow<ImmutableList<ProductRowViewModel>?>(null)

                viewModelScope.launch {
                    result.value = productRepository.getProducts(searchQuery)
                        .map { product ->
                            ProductRowViewModel(
                                product = product,
                                select = { selectProduct(product.id) }
                            )
                        }
                        .toImmutableList()
                }

                return@flatMapLatest result
            }
            .cacheLatest(initialValue = null)
    }
}