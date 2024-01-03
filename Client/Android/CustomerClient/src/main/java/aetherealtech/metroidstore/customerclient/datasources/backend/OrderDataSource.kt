package aetherealtech.metroidstore.customerclient.datasources.backend

import aetherealtech.metroidstore.customerclient.backendclient.AuthenticatedBackendClient
import aetherealtech.metroidstore.customerclient.datasources.api.OrderDataSource
import aetherealtech.metroidstore.customerclient.model.OrderID

class OrderDataSourceBackend(
    private val client: AuthenticatedBackendClient
): OrderDataSource {
    override suspend fun getOrders() = client.getOrders()
    override suspend fun getOrder(id: OrderID) = client.getOrder(id)
    override suspend fun getOrderActivity(id: OrderID) = client.getOrderActivity(id)
}