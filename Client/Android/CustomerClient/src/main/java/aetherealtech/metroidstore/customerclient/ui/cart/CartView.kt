package aetherealtech.metroidstore.customerclient.ui.cart

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
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.cartrow.CartRowView
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.BusyView
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CartView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: CartViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Cart"
        ))
    }

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

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    MetroidStoreTheme {
        CartView(
            setAppBarState = { },
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