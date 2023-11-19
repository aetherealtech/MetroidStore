package com.example.metroidstore.addtocart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.ui.theme.Colors
import com.example.metroidstore.widgets.BottomModal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Composable
fun AddToCartButton(
    viewModel: AddToCartViewModel
) {
    Button(
        onClick = { viewModel.addToCart() },
        modifier = Modifier.fillMaxWidth(0.75f),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Colors.PrimaryCallToAction
        )
    ) {
        Text(text = "Add to Cart")
    }

    AddToCartConfirmation(
        viewModel = viewModel.confirmationViewModel
    )
}

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
            _confirmationViewModel.emit(AddToCartConfirmationViewModel(
                product = product,
                cartRepository = cartRepository,
                viewCart = viewCart
            ))
        }
    }
}