package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.NewAddress
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import aetherealtech.metroidstore.customerclient.utilities.mapState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserRepository(
    private val dataSource: UserDataSource
) {
    private val _addresses = MutableStateFlow<ImmutableList<UserAddressSummary>>(persistentListOf())
    private val _shippingMethods =
        MutableStateFlow<ImmutableList<ShippingMethod>>(persistentListOf())
    private val _paymentMethods =
        MutableStateFlow<ImmutableList<PaymentMethodSummary>>(persistentListOf())

    private val _addressDetails = MutableStateFlow<ImmutableList<UserAddressDetails>>(persistentListOf())

    private val _processes = MutableStateFlow(0)

    val addresses = _addresses
        .asStateFlow()

    val shippingMethods = _shippingMethods
        .asStateFlow()

    val paymentMethods = _paymentMethods
        .asStateFlow()

    val addressDetails = _addressDetails
        .asStateFlow()

    val busy = _processes
        .mapState { processes -> processes > 0 }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _addressDetails.collect { addressDetailsList ->
                _addresses.value = addressDetailsList
                    .map { addressDetails ->
                        UserAddressSummary(
                            addressID = addressDetails.address.id,
                            name = addressDetails.name,
                            isPrimary = addressDetails.isPrimary
                        )
                    }
                    .toImmutableList()
            }
        }
    }

    suspend fun updateAddresses() {
        update { _addresses.value = dataSource.getAddresses() }
    }

    suspend fun updateShippingMethods() {
        update { _shippingMethods.value = dataSource.getShippingMethods() }
    }

    suspend fun updatePaymentMethods() {
        update { _paymentMethods.value = dataSource.getPaymentMethods() }
    }

    suspend fun updateAddressDetails() {
        update { _addressDetails.value = dataSource.getAddressDetails() }
    }

    suspend fun placeOrder(order: NewOrder) = dataSource.placeOrder(order)

    private suspend fun update(
        action: suspend () -> Unit
    ) {
        _processes.update { processes -> processes + 1 }
        action()
        _processes.update { processes -> processes - 1 }
    }

    suspend fun createAddress(address: NewAddress) {
        update {  _addressDetails.value = dataSource.createAddress(address) }
    }
}