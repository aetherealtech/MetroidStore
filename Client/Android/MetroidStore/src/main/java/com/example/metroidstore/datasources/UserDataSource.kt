package com.example.metroidstore.datasources

import com.example.metroidstore.model.PaymentMethodSummary
import com.example.metroidstore.model.ShippingMethod
import com.example.metroidstore.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList

interface UserDataSource {
    suspend fun getAddresses(): ImmutableList<UserAddressSummary>
    suspend fun getShippingMethods(): ImmutableList<ShippingMethod>
    suspend fun getPaymentMethods(): ImmutableList<PaymentMethodSummary>
}