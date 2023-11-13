package com.example.metroidstore.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.metroidstore.model.ProductDetails

@Composable
fun AddToCartConfirmation(
    viewModel: AddToCartConfirmationViewModel,
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
class AddToCartConfirmationViewModel(
    private val product: ProductDetails
): ViewModel() {
    val text: String

    init {
        text = "Adding ${product.name} to Cart..."
    }
}