package aetherealtech.metroidstore.customerclient.datasources

import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList

interface UserDataSource {
    suspend fun getAddresses(): ImmutableList<UserAddressSummary>
    suspend fun getShippingMethods(): ImmutableList<ShippingMethod>
    suspend fun getPaymentMethods(): ImmutableList<PaymentMethodSummary>
    suspend fun getAddressDetails(): ImmutableList<UserAddressDetails>

    suspend fun placeOrder(order: NewOrder): OrderID
}