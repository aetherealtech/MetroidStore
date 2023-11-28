package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.NewAddress
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import kotlinx.collections.immutable.ImmutableList

class UserDataSourceBackend(
    private val client: BackendClient
): UserDataSource {
    override suspend fun getAddresses() = client.getAddresses()
    override suspend fun getShippingMethods() = client.getShippingMethods()
    override suspend fun getPaymentMethods() = client.getPaymentMethods()
    override suspend fun getAddressDetails() = client.getAddressDetails()

    override suspend fun placeOrder(order: NewOrder) = client.placeOrder(order)
    override suspend fun createAddress(address: NewAddress) = client.createAddress(address)
}