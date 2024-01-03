package aetherealtech.metroidstore.customerclient.ui.orders

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aetherealtech.metroidstore.customerclient.ui.ordersummaryrow.OrderSummaryRow
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import androidx.compose.runtime.LaunchedEffect

@Composable
fun OrdersView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: OrdersViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Orders"
        ))
    }

    AsyncLoadedShimmering(
        modifier = modifier,
        data = viewModel.items
    ) { modifier, items ->
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp)
        ) {
            items(items) { rowViewModel ->
                OrderSummaryRow(
                    viewModel = rowViewModel
                )
            }
        }
    }
}

