package aetherealtech.metroidstore.customerclient.ui.addtocartconfirmation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.datasources.fake.details
import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.uitoolkit.ConfirmationModal
import aetherealtech.metroidstore.customerclient.widgets.CloseableView
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import kotlinx.coroutines.flow.Flow

@Composable
fun AddToCartConfirmation(
    viewModel: Flow<AddToCartConfirmationViewModel>
) {
    ConfirmationModal(
        viewModel = viewModel
    ) { contentViewModel, onClose ->
        AddedToCartView(
            viewModel = contentViewModel,
            onClose = onClose
        )
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