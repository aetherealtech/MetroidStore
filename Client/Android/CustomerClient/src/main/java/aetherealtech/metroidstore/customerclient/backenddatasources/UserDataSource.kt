package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.NewOrder

class UserDataSourceBackend(
    private val client: BackendClient
): UserDataSource {
    override suspend fun getAddresses() = client.getAddresses()
    override suspend fun getShippingMethods() = client.getShippingMethods()
    override suspend fun getPaymentMethods() = client.getPaymentMethods()

    override suspend fun placeOrder(order: NewOrder) = client.placeOrder(order)
}