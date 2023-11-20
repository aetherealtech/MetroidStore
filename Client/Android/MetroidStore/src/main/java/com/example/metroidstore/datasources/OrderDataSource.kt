package com.example.metroidstore.datasources

import com.example.metroidstore.model.OrderSummary
import kotlinx.collections.immutable.ImmutableList

interface OrderDataSource {
    suspend fun getOrders(): ImmutableList<OrderSummary>
}