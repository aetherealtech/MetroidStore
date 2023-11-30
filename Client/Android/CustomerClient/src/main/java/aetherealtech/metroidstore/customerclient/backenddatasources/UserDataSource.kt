package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import kotlinx.collections.immutable.ImmutableList

class UserDataSourceBackend(
    private val client: BackendClient
): UserDataSource {
    override suspend fun getAddresses() = client.getAddresses()
    override suspend fun getShippingMethods() = client.getShippingMethods()
    override suspend fun getPaymentMethods() = client.getPaymentMethods()
    override suspend fun getAddressDetails() = client.getAddressDetails()
    override suspend fun getPaymentMethodDetails() = client.getPaymentMethodDetails()

    override suspend fun placeOrder(order: NewOrder) = client.placeOrder(order)

    override suspend fun createAddress(address: EditAddress) = client.createAddress(address)
    override suspend fun updateAddress(address: EditAddress, id: Address.ID) = client.updateAddress(address, id)
    override suspend fun deleteAddress(id: Address.ID) = client.deleteAddress(id)

    override suspend fun createPaymentMethod(paymentMethod: EditPaymentMethod) = client.createPaymentMethod(paymentMethod)
    override suspend fun updatePaymentMethod(paymentMethod: EditPaymentMethod, id: PaymentMethodID) = client.updatePaymentMethod(paymentMethod, id)
    override suspend fun deletePaymentMethod(id: PaymentMethodID) = client.deletePaymentMethod(id)
}