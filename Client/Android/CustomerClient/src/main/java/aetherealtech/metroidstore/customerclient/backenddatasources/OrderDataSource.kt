package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.OrderDataSource
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID

class OrderDataSourceBackend(
    private val client: BackendClient
): OrderDataSource {
    override suspend fun getOrders() = client.getOrders()
    override suspend fun getOrder(id: OrderID) = client.getOrder(id)
}