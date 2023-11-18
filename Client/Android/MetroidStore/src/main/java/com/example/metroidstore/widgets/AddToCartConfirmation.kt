package com.example.metroidstore.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.cart.CartRowView
import com.example.metroidstore.cart.CartRowViewModel
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.fakedatasources.cartItem
import com.example.metroidstore.fakedatasources.details
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
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
    private val cartRepository: CartRepository,
    private val viewCart: () -> Unit
): ViewModel() {
    private val _addedViewModel = MutableStateFlow<AddedToCartViewModel?>(null)

    val addedViewModel = _addedViewModel
        .asStateFlow()
    init {
        viewModelScope.launch {
            cartRepository.addToCart(product.id)

            _addedViewModel.value = AddedToCartViewModel(
                product = product,
                viewCart = viewCart
            )
        }
    }
}

@Composable
fun AddedToCartView(
    viewModel: AddedToCartViewModel,
    onClose: () -> Unit,
) {
    CloseableView(onClose = onClose) {
        Column(
            modifier = Modifier.height(256.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.text
            )
            Box(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                PrimaryCallToAction(
                    onClick = { viewModel.viewCart() },
                    text = "View Cart"
                )
            }
        }
    }
}
class AddedToCartViewModel(
    private val product: ProductDetails,
    val viewCart: () -> Unit
): ViewModel() {
    val text: String

    init {
        text = "Added ${product.name} to Cart"
    }
}

@Preview(showBackground = true)
@Composable
fun AddedToCartPreview() {
    MetroidStoreTheme {
        AddedToCartView(
            viewModel = AddedToCartViewModel(
                product = DataSourceFake.fakeProducts[0].details,
                viewCart = { }
            ),
            onClose = { }
        )
    }
}