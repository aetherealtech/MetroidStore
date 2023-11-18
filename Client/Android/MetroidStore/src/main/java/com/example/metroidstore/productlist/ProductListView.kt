package com.example.metroidstore.productlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun ProductListView(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel
) {
    val items by viewModel.items.collectAsState()

    LazyColumn(modifier = modifier) {
        this.items(items) { rowViewModel ->
            ProductRowView(
                viewModel = rowViewModel
            )
        }
    }
}

class ProductListViewModel(
    productRepository: ProductRepository,
    selectProduct: (ProductID) -> Unit
): ViewModel() {

    private val _items = MutableStateFlow<ImmutableList<ProductRowViewModel>>(persistentListOf())

    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            _items.value = productRepository.getProducts()
                .map { product -> ProductRowViewModel(
                    product = product,
                    select = { selectProduct(product.id) }
                ) }
                .toImmutableList()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    MetroidStoreTheme {
        ProductListView(
            viewModel = ProductListViewModel(
                productRepository = ProductRepository(
                    dataSource = DataSourceFake()
                ),
                selectProduct = { }
            )
        )
    }
}