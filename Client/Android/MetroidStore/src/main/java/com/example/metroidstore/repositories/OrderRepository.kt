package com.example.metroidstore.repositories

import com.example.metroidstore.datasources.OrderDataSource
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.OrderSummary
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

    private suspend fun update(
        action: suspend () -> ImmutableList<OrderSummary>
    ) {
        _busy.value = true
        _orders.value = action()
        _busy.value = false
    }
}