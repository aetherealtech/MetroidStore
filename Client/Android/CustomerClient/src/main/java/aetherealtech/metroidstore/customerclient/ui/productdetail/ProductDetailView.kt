package aetherealtech.metroidstore.customerclient.ui.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.ui.addtocartbutton.AddToCartButton
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.ImagesCarousel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProductDetailView(
    setAppBarState: (AppBarState) -> Unit,
    viewModel: ProductDetailViewModel
) {
    val name by viewModel.name.collectAsState()

    LaunchedEffect(name) {
        setAppBarState(AppBarState(
            title = name ?: "Loading..."
        ))
    }

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

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    MetroidStoreTheme {
        ProductDetailView(
            setAppBarState = { },
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