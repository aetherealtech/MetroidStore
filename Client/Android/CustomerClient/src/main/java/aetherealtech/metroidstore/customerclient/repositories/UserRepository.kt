package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.api.UserDataSource
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import aetherealtech.kotlinflowsextensions.mapState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val _paymentMethodDetails = MutableStateFlow<ImmutableList<PaymentMethodDetails>>(persistentListOf())

    private val _processes = MutableStateFlow(0)

    val addresses = _addresses
        .asStateFlow()

    val shippingMethods = _shippingMethods
        .asStateFlow()

    val paymentMethods = _paymentMethods
        .asStateFlow()

    val addressDetails = _addressDetails
        .asStateFlow()

    val paymentMethodDetails = _paymentMethodDetails
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

        CoroutineScope(Dispatchers.IO).launch {
            _paymentMethodDetails.collect { paymentMethodDetailsList ->
                _paymentMethods.value = paymentMethodDetailsList
                    .map { paymentMethodDetails ->
                        PaymentMethodSummary(
                            id = paymentMethodDetails.id,
                            name = paymentMethodDetails.name,
                            isPrimary = paymentMethodDetails.isPrimary
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

    suspend fun updatePaymentMethodDetails() {
        update { _paymentMethodDetails.value = dataSource.getPaymentMethodDetails() }
    }

    suspend fun placeOrder(order: NewOrder) = dataSource.placeOrder(order)

    suspend fun createAddress(address: EditAddress) {
        update { _addressDetails.value = dataSource.createAddress(address) }
    }

    suspend fun updateAddress(address: EditAddress, id: Address.ID) {
        update { _addressDetails.value = dataSource.updateAddress(address, id) }
    }

    suspend fun deleteAddress(id: Address.ID) {
        update { _addressDetails.value = dataSource.deleteAddress(id) }
    }

    suspend fun createPaymentMethod(paymentMethod: EditPaymentMethod) {
        update { _paymentMethodDetails.value = dataSource.createPaymentMethod(paymentMethod) }
    }

    suspend fun updatePaymentMethod(paymentMethod: EditPaymentMethod, id: PaymentMethodID) {
        update { _paymentMethodDetails.value = dataSource.updatePaymentMethod(paymentMethod, id) }
    }

    suspend fun deletePaymentMethod(id: PaymentMethodID) {
        update { _paymentMethodDetails.value = dataSource.deletePaymentMethod(id) }
    }

    private suspend fun update(
        action: suspend () -> Unit
    ) {
        _processes.update { processes -> processes + 1 }
        action()
        _processes.update { processes -> processes - 1 }
    }
}