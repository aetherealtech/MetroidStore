package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.OrderDataSource
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class OrderDataSourceFake: OrderDataSource {
    override suspend fun getOrders(): ImmutableList<OrderSummary> {
        return persistentListOf()
    }
}