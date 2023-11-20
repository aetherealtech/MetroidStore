package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.OrderDataSource

class OrderDataSourceBackend(
    private val client: BackendClient
): OrderDataSource {
    override suspend fun getOrders() = client.getOrders()
}