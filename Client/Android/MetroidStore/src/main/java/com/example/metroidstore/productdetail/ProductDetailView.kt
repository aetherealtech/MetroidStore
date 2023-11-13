package com.example.metroidstore.productdetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.model.Product
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.utilities.mapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun ProductDetailView(
    viewModel: ProductDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val currentName by viewModel.name.collectAsState()

    Text(currentName)
}

class ProductDetailViewModel(
    productId: Product.ID,
    repository: ProductRepository
): ViewModel() {
    private val _name = MutableStateFlow<String?>(null)

    val name = _name
        .mapState { name -> name ?: "Loading..." }

    init {
        viewModelScope.launch {
            val product = repository.getProductDetails(productId)

            _name.value = product.name
        }
    }
}