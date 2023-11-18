package com.example.metroidstore.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.itemCount
import com.example.metroidstore.model.subtotal
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.widgets.AsyncLoadedShimmering
import com.example.metroidstore.widgets.BusyView
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import com.example.metroidstore.widgets.PrimaryCallToAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun CartView(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel
) {
    BusyView(
        busy = viewModel.busy
    ) {
        AsyncLoadedShimmering(
            modifier = modifier,
            data = viewModel.items
        ) { modifier, items ->
            LazyColumn(
                modifier = modifier
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    CartSummaryView(
                        viewModel = viewModel.summary
                    )
                }

                items(items) { rowViewModel ->
                    CartRowView(
                        viewModel = rowViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun CartSummaryView(
    modifier: Modifier = Modifier,
    viewModel: CartSummaryViewModel
) {
    val subtotal by viewModel.subtotal.collectAsState()
    val primaryActionTitle by viewModel.primaryActionTitle.collectAsState()

    Column(
        modifier = modifier
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
                viewModel = subtotal
            )
        }

        PrimaryCallToAction(
            onClick = { viewModel.proceedToCheckout() },
            text = primaryActionTitle
        )
    }
}
class CartViewModel(
    private val repository: CartRepository,
    selectItem: (ProductID) -> Unit,
    proceedToCheckout: () -> Unit
): ViewModel() {
    val busy = repository.busy

    val items = repository.cart
        .mapState { cart ->
            cart
                .map { cartItem ->
                    CartRowViewModel(
                        repository = repository,
                        product = cartItem,
                        select = { selectItem(cartItem.productID) }
                    )
                }
                .toImmutableList()
        }

    val summary = CartSummaryViewModel(
        cart = repository.cart,
        proceedToCheckout = proceedToCheckout
    )

    init {
        viewModelScope.launch {
            repository.updateCart()
        }
    }
}

class CartSummaryViewModel(
    cart: StateFlow<ImmutableList<CartItem>>,
    val proceedToCheckout: () -> Unit
): ViewModel() {
    val subtotal = cart
        .mapState { cart -> PriceViewModel(cart.subtotal) }

    val primaryActionTitle = cart
        .mapState { cart -> "Proceed to Checkout (${cart.itemCount} items)" }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    MetroidStoreTheme {
        CartView(
            viewModel = CartViewModel(
                repository = CartRepository(
                    dataSource = DataSourceFake().cart
                ),
                selectItem = { },
                proceedToCheckout = { }
            )
        )
    }
}