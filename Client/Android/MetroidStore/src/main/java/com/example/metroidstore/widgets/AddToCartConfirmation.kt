package com.example.metroidstore.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.repositories.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun AddToCartConfirmation(
    viewModel: AddToCartConfirmationViewModel,
    onClose: () -> Unit,
) {
    val currentAddedToCartViewModel by viewModel.addedViewModel.collectAsState()

    currentAddedToCartViewModel?.let { addedToCartViewModel ->
        AddedToCartView(
            viewModel = addedToCartViewModel,
            onClose = onClose
        )
    } ?: run {
        CircularProgressIndicator()
    }
}

class AddToCartConfirmationViewModel(
    private val product: ProductDetails,
    private val cartRepository: CartRepository
): ViewModel() {
    private val _addedViewModel = MutableStateFlow<AddedToCartViewModel?>(null)

    val addedViewModel = _addedViewModel
        .asStateFlow()
    init {
        viewModelScope.launch {
            cartRepository.addToCart(product.id)

            _addedViewModel.value = AddedToCartViewModel(product)
        }
    }
}

@Composable
fun AddedToCartView(
    viewModel: AddedToCartViewModel,
    onClose: () -> Unit,
) {
    CloseableView(onClose = onClose) {
        Column {
            Text(
                text = viewModel.text,
                modifier = Modifier.height(256.dp)
            )
        }
    }
}
class AddedToCartViewModel(
    private val product: ProductDetails
): ViewModel() {
    val text: String

    init {
        text = "Added ${product.name} to Cart"
    }
}