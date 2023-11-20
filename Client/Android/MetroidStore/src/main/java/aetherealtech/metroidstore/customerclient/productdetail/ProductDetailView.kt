package aetherealtech.metroidstore.customerclient.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.parallelMap
import aetherealtech.metroidstore.customerclient.addtocart.AddToCartButton
import aetherealtech.metroidstore.customerclient.addtocart.AddToCartViewModel
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.ImagesCarousel
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun ProductDetailView(
    viewModel: ProductDetailViewModel
) {
    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncLoadedShimmering(
            data = viewModel.name
        ) { _, currentName ->
            Text(
                text = currentName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }

        ImagesCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f),
            data = viewModel.images
        )

        AsyncLoadedShimmering(
            data = viewModel.addToCartViewModel
        ) { _, addToCartViewModel ->
            AddToCartButton(
                viewModel = addToCartViewModel
            )
        }
    }
}

class ProductDetailViewModel(
    productID: ProductID,
    repository: ProductRepository,
    private val viewCart: () -> Unit
): ViewModel() {
    private val _name = MutableStateFlow<String?>(null)
    private val _images = MutableStateFlow<ImmutableList<ImageBitmap>?>(null)

    private val _addToCartViewModel = MutableStateFlow<AddToCartViewModel?>(null)

    val name = _name
        .asStateFlow()

    val images = _images
        .asStateFlow()

    val addToCartViewModel = _addToCartViewModel
        .asStateFlow()

    init {
        viewModelScope.launch {
            val product = repository.getProductDetails(productID)

            _name.value = product.name

            _images.value = product.images
                .parallelMap { imageSource -> imageSource.load().asImageBitmap() }
                .toImmutableList()

            _addToCartViewModel.value = AddToCartViewModel(
                product = product,
                cartRepository = repository.cart,
                viewCart = viewCart
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    MetroidStoreTheme {
        ProductDetailView(
            viewModel = ProductDetailViewModel(
                productID = ProductID(0),
                repository = ProductRepository(
                    dataSource = DataSourceFake()
                ),
                viewCart = { }
            )
        )
    }
}