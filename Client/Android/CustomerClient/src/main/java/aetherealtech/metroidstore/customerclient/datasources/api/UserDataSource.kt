package aetherealtech.metroidstore.customerclient.datasources.api

import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
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

    suspend fun createAddress(address: EditAddress): ImmutableList<UserAddressDetails>
    suspend fun updateAddress(address: EditAddress, id: Address.ID): ImmutableList<UserAddressDetails>
    suspend fun deleteAddress(id: Address.ID): ImmutableList<UserAddressDetails>
    suspend fun getPaymentMethodDetails(): ImmutableList<PaymentMethodDetails>
    suspend fun createPaymentMethod(paymentMethod: EditPaymentMethod): ImmutableList<PaymentMethodDetails>
    suspend fun updatePaymentMethod(paymentMethod: EditPaymentMethod, id: PaymentMethodID): ImmutableList<PaymentMethodDetails>
    suspend fun deletePaymentMethod(id: PaymentMethodID): ImmutableList<PaymentMethodDetails>
}