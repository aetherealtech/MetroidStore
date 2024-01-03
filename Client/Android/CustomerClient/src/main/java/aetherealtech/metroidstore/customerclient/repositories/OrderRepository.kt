package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.api.OrderDataSource
import aetherealtech.metroidstore.customerclient.model.OrderActivity
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderRepository(
    private val dataSource: OrderDataSource
) {
    private val _orders = MutableStateFlow<ImmutableList<OrderSummary>>(persistentListOf())

    private val _busy = MutableStateFlow(false)

    val orders = _orders
        .asStateFlow()

    val busy = _busy
        .asStateFlow()

    suspend fun updateOrders() {
        update { dataSource.getOrders() }
    }

    suspend fun getOrder(id: OrderID): OrderDetails {
        return dataSource.getOrder(id)
    }

    suspend fun getOrderActivity(id: OrderID): ImmutableList<OrderActivity> {
        return dataSource.getOrderActivity(id)
    }

    private suspend fun update(
        action: suspend () -> ImmutableList<OrderSummary>
    ) {
        _busy.value = true
        _orders.value = action()
        _busy.value = false
    }
}