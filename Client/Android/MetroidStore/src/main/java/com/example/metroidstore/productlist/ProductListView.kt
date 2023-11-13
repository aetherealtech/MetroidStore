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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.metroidstore.MainViewModel
import com.example.metroidstore.fakedatasources.ProductDataSourceFake
import com.example.metroidstore.model.Product
import com.example.metroidstore.productdetail.ProductDetailViewModel
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun ProductListView(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel,
    openProductDetails: (Product.ID) -> Unit
) {
    val items by viewModel.items.collectAsState()

    LazyColumn(modifier = modifier) {
        this.items(items) { rowViewModel ->
            ProductRowView(
                modifier = Modifier.clickable {
                    openProductDetails(rowViewModel.id)
                },
                viewModel = rowViewModel
            )
        }
    }
}

class ProductListViewModel(
    productRepository: ProductRepository
): ViewModel() {

    private val _items = MutableStateFlow(emptyList<ProductRowViewModel>())

    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            _items.value = productRepository.getProducts()
                .map { product -> ProductRowViewModel(product) }
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
                    dataSource = ProductDataSourceFake()
                )
            ),
            openProductDetails = { }
        )
    }
}