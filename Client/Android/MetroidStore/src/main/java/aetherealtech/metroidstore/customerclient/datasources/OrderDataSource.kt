package aetherealtech.metroidstore.customerclient.datasources

import aetherealtech.metroidstore.customerclient.model.OrderSummary
import kotlinx.collections.immutable.ImmutableList

interface OrderDataSource {
    suspend fun getOrders(): ImmutableList<OrderSummary>
}