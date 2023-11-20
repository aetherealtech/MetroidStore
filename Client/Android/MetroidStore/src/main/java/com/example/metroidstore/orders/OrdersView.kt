package com.example.metroidstore.orders

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.cart.CartRowView
import com.example.metroidstore.model.OrderID
import com.example.metroidstore.repositories.OrderRepository
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.widgets.AsyncLoadedShimmering
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

@Composable
fun OrdersView(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
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