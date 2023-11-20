package com.example.metroidstore.fakedatasources

import com.example.metroidstore.datasources.OrderDataSource
import com.example.metroidstore.model.OrderSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class OrderDataSourceFake: OrderDataSource {
    override suspend fun getOrders(): ImmutableList<OrderSummary> {
        return persistentListOf()
    }
}