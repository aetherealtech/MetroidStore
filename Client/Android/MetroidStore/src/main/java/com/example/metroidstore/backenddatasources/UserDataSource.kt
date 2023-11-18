package com.example.metroidstore.backenddatasources

import com.example.metroidstore.backendclient.BackendClient
import com.example.metroidstore.datasources.UserDataSource
import com.example.metroidstore.model.NewOrder
import com.example.metroidstore.model.PaymentMethodSummary
import com.example.metroidstore.model.ShippingMethod
import com.example.metroidstore.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList

class UserDataSourceBackend(
    private val client: BackendClient
): UserDataSource {
    override suspend fun getAddresses() = client.getAddresses()
    override suspend fun getShippingMethods() = client.getShippingMethods()
    override suspend fun getPaymentMethods() = client.getPaymentMethods()

    override suspend fun placeOrder(order: NewOrder) = client.placeOrder(order)
}