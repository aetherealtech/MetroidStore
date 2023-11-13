package com.example.metroidstore.widgets

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
            containerColor = Color(0xFFFFDD00)
        )
    ) {
        Text(text = "Add to Cart")
    }

    BottomModal(
        data = viewModel.confirmationViewModel
    ) { confirmationViewModel, onClose ->
        AddToCartConfirmation(
            viewModel = confirmationViewModel,
            onClose = onClose
        )
    }
}

class AddToCartViewModel(
    private val product: ProductDetails
): ViewModel() {
    val _confirmationViewModel = MutableSharedFlow<AddToCartConfirmationViewModel>()

    val confirmationViewModel = _confirmationViewModel
        .asSharedFlow()

    fun addToCart() {
        viewModelScope.launch {
            _confirmationViewModel.emit( AddToCartConfirmationViewModel(product = product))
        }
    }
}