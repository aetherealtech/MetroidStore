package aetherealtech.metroidstore.customerclient.ui.orders

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.ui.ordersummaryrow.OrderSummaryRow
import aetherealtech.metroidstore.customerclient.ui.ordersummaryrow.OrderSummaryRowViewModel
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

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

class OrdersViewModel(
    repository: OrderRepository,
    viewOrder: (OrderID) -> Unit
): ViewModel() {
    val items  = repository.orders
        .mapState { orders ->
            orders
                .map { order ->
                    OrderSummaryRowViewModel(
                        order = order,
                        viewOrder = viewOrder
                    )
                }
                .toImmutableList()
        }

    init {
        viewModelScope.launch {
            repository.updateOrders()
        }
    }
}