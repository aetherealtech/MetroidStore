package aetherealtech.metroidstore.customerclient.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.itemCount
import aetherealtech.metroidstore.customerclient.model.subtotal
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.BusyView
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToActionViewModel
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
    val primaryAction by viewModel.primaryAction.collectAsState()

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
            viewModel = primaryAction
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
    private val proceedToCheckout: () -> Unit
): ViewModel() {
    val subtotal = cart
        .mapState { cart -> PriceViewModel(cart.subtotal) }

    val primaryAction = cart
        .mapState { cart ->
            PrimaryCallToActionViewModel(
                action = if (cart.isEmpty()) null else proceedToCheckout,
                text = "Proceed to Checkout (${cart.itemCount} items)"
            )
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
                ),
                selectItem = { },
                proceedToCheckout = { }
            )
        )
    }
}