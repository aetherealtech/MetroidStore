package com.example.metroidstore.repositories

import com.example.metroidstore.datasources.UserDataSource
import com.example.metroidstore.model.Address
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.NewOrder
import com.example.metroidstore.model.PaymentMethodSummary
import com.example.metroidstore.model.ShippingMethod
import com.example.metroidstore.model.UserAddressSummary
import com.example.metroidstore.utilities.mapState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserRepository(
    private val dataSource: UserDataSource
) {
    private val _addresses = MutableStateFlow<ImmutableList<UserAddressSummary>>(persistentListOf())
    private val _shippingMethods =
        MutableStateFlow<ImmutableList<ShippingMethod>>(persistentListOf())
    private val _paymentMethods =
        MutableStateFlow<ImmutableList<PaymentMethodSummary>>(persistentListOf())

    private val _processes = MutableStateFlow(0)

    val addresses = _addresses
        .asStateFlow()

    val shippingMethods = _shippingMethods
        .asStateFlow()

    val paymentMethods = _paymentMethods
        .asStateFlow()

    val busy = _processes
        .mapState { processes -> processes > 0 }

    suspend fun updateAddresses() {
        update { _addresses.value = dataSource.getAddresses() }
    }

    suspend fun updateShippingMethods() {
        update { _shippingMethods.value = dataSource.getShippingMethods() }
    }

    suspend fun updatePaymentMethods() {
        update { _paymentMethods.value = dataSource.getPaymentMethods() }
    }

    suspend fun placeOrder(
        addressID: Address.ID,
        shippingMethod: ShippingMethod,
        paymentMethodID: PaymentMethodSummary.ID
    ) {
        dataSource.placeOrder(NewOrder(
            addressID = addressID,
            shippingMethod = shippingMethod,
            paymentMethodID = paymentMethodID
        ))
    }

    private suspend fun update(
        action: suspend () -> Unit
    ) {
        _processes.update { processes -> processes + 1 }
        action()
        _processes.update { processes -> processes - 1 }
    }
}