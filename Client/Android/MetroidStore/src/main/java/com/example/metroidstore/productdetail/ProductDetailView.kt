package com.example.metroidstore.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.fakedatasources.ProductDataSourceFake
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.utilities.parallelMap
import com.example.metroidstore.widgets.AddToCartButton
import com.example.metroidstore.widgets.ImagesCarousel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun ProductDetailView(
    viewModel: ProductDetailViewModel
) {
    val currentName by viewModel.name.collectAsState()

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = currentName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium
        )

        ImagesCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f),
            data = viewModel.images
        )

        AddToCartButton(
            onClick = { viewModel.addToCart() }
        )
    }
}

class ProductDetailViewModel(
    productId: ProductID,
    repository: ProductRepository
): ViewModel() {
    private val _name = MutableStateFlow<String?>(null)
    private val _images = MutableStateFlow<ImmutableList<ImageBitmap>?>(null)

    val name = _name
        .mapState { name -> name ?: "Loading..." }

    val images = _images
        .asStateFlow()

    init {
        viewModelScope.launch {
            val product = repository.getProductDetails(productId)

            _name.value = product.name

            _images.value = product.images
                .parallelMap { imageSource -> imageSource.load() }
                .toImmutableList()
        }
    }

    fun addToCart() {
        println("Added ${name.value} to cart")
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    MetroidStoreTheme {
        ProductDetailView(
            viewModel = ProductDetailViewModel(
                productId = ProductID(0),
                repository = ProductRepository(
                    dataSource = ProductDataSourceFake()
                )
            )
        )
    }
}