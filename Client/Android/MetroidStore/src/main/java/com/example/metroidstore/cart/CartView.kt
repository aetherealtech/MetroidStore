package com.example.metroidstore.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.widgets.AsyncLoadedShimmering
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun CartView(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel,
    openProductDetails: (ProductID) -> Unit
) {
    val busy by viewModel.busy.collectAsState()

    Box(contentAlignment = Alignment.Center) {
        if(busy) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
            )
        }
        AsyncLoadedShimmering(
            modifier = modifier,
            data = viewModel.cart
        ) { modifier, cart ->
            LazyColumn(
                modifier = modifier
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    CartSummaryView(cart = cart) {
                        viewModel.proceedToCheckout()
                    }
                }

                items(cart.items) { rowViewModel ->
                    CartRowView(
                        modifier = Modifier.clickable {
                            openProductDetails(rowViewModel.id)
                        },
                        viewModel = rowViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun CartSummaryView(
    cart: CartViewModel.Cart,
    proceedToCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Subtotal",
                fontSize = 18.sp
            )
            PriceView(
                viewModel = cart.subtotal
            )
        }

        Button(
            onClick = { proceedToCheckout() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = Color(0xFFFFDD00)
            )
        ) {
            Text(text = "Proceed to Checkout (${cart.itemCount} items)")
        }
    }
}
class CartViewModel(
    private val repository: CartRepository
): ViewModel() {
    data class Cart(
        val subtotal: PriceViewModel,
        val itemCount: Int,
        val items: ImmutableList<CartRowViewModel>
    )

    val cart = repository.cart
        .mapState { cart ->
            val subtotal = cart
                .map { item -> item.price }
                .fold(Price.zero) { lhs, rhs -> lhs + rhs }

            val itemCount = cart
                .map { item -> item.quantity }
                .fold(0) { lhs, rhs -> lhs + rhs }

            val items = cart
                .map { cartItem -> CartRowViewModel(
                    repository = repository,
                    product = cartItem
                ) }
                .toImmutableList()

            return@mapState Cart(
                subtotal = PriceViewModel(price = subtotal),
                itemCount = itemCount,
                items = items
            )
        }

    val busy = repository.busy
    init {
        viewModelScope.launch {
            repository.updateCart()
        }
    }

    fun proceedToCheckout() {

    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    MetroidStoreTheme {
        CartView(
            viewModel = CartViewModel(
                repository = CartRepository(
                    dataSource = DataSourceFake().cart
                )
            ),
            openProductDetails = { }
        )
    }
}