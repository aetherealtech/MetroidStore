package aetherealtech.metroidstore.customerclient.ui.cartrow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.datasources.fake.cartItem
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ImageSource
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncImage
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import aetherealtech.metroidstore.customerclient.widgets.QuantityControl
import aetherealtech.metroidstore.customerclient.widgets.ShadowButton
import kotlinx.coroutines.launch

@Composable
fun CartRowView(
    modifier: Modifier = Modifier,
    viewModel: CartRowViewModel
) {
    Box(
        modifier = modifier
            .clickable { viewModel.select() }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier.height(128.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxHeight(),
                    source = viewModel.image,
                    contentDescription = "Product Image"
                )
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = viewModel.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    PriceView(viewModel = viewModel.price)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
                    .height(64.dp)
            ) {
                QuantityControl(
                    quantity = viewModel.quantity,
                    onDecrement = { viewModel.decrementQuantity() },
                    onIncrement = { viewModel.incrementQuantity() }
                )

                ShadowButton(
                    text = "Delete",
                    onClick = { viewModel.delete() }
                )
            }
        }
        Divider()
    }
}

class CartRowViewModel(
    private val repository: CartRepository,
    product: CartItem,
    val select: () -> Unit,
): ViewModel() {
    val id: ProductID
    val image: ImageSource
    val name: String
    val price: PriceViewModel

    val quantity = product.quantity

    init {
        id = product.productID
        image = product.image
        name = product.name
        price = PriceViewModel(product.pricePerUnit)
    }

    fun decrementQuantity() {
        viewModelScope.launch {
            repository.decrementQuantity(id)
        }
    }

    fun incrementQuantity() {
        viewModelScope.launch {
            repository.incrementQuantity(id)
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.removeFromCart(id)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartRowPreview() {
    MetroidStoreTheme {
        CartRowView(
            viewModel = CartRowViewModel(
                repository = CartRepository(dataSource = DataSourceFake().cart),
                product = DataSourceFake.fakeProducts[0].cartItem(quantity = 3),
                select = { }
            )
        )
    }
}