package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import aetherealtech.metroidstore.customerclient.utilities.mapState
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

    suspend fun placeOrder(order: NewOrder) = dataSource.placeOrder(order)

    private suspend fun update(
        action: suspend () -> Unit
    ) {
        _processes.update { processes -> processes + 1 }
        action()
        _processes.update { processes -> processes - 1 }
    }
}