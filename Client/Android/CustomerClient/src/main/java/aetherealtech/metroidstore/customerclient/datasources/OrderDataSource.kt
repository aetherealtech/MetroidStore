package aetherealtech.metroidstore.customerclient.datasources

import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import kotlinx.collections.immutable.ImmutableList

interface OrderDataSource {
    suspend fun getOrders(): ImmutableList<OrderSummary>
    suspend fun getOrder(id: OrderID): OrderDetails
}