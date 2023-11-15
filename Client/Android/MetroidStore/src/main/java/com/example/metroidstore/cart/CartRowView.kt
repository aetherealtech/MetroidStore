package com.example.metroidstore.cart

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
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.fakedatasources.cartItem
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.ImageSource
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.widgets.AsyncImage
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import com.example.metroidstore.widgets.QuantityControl
import com.example.metroidstore.widgets.ShadowButton

@Composable
fun CartRowView(
    modifier: Modifier = Modifier,
    viewModel: CartRowViewModel
) {
    Box(
        modifier = modifier
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
    repository: CartRepository,
    product: CartItem
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
        price = PriceViewModel(product.price)
    }

    fun decrementQuantity() {
//        if(_quantity.value == 1) {
//            delete()
//        } else {
//            _quantity.value -= 1
//        }
    }

    fun incrementQuantity() {
//        _quantity.value += 1
    }

    fun delete() {

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
            )
        )
    }
}