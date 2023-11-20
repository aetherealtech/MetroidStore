package com.example.metroidstore.backenddatasources

import com.example.metroidstore.backendclient.BackendClient
import com.example.metroidstore.datasources.OrderDataSource

class OrderDataSourceBackend(
    private val client: BackendClient
): OrderDataSource {
    override suspend fun getOrders() = client.getOrders()
}