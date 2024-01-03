package aetherealtech.metroidstore.customerclient.ui.orders

import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.ui.ordersummaryrow.OrderSummaryRowViewModel
import aetherealtech.metroidstore.customerclient.utilities.mapState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

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