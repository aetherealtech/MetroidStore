package aetherealtech.metroidstore.customerclient.ui.paymentmethods

import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.ui.paymentmethodrow.PaymentMethodRowViewModel
import aetherealtech.kotlinflowsextensions.mapState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PaymentMethodsViewModel(
    private val repository: UserRepository,
    val openAddPaymentMethod: () -> Unit,
    val openEditPaymentMethod: (PaymentMethodID) -> Unit
): ViewModel() {
    val items = repository.paymentMethodDetails
        .mapState { paymentMethodDetailsList ->
            paymentMethodDetailsList.map { paymentMethodDetails ->
                PaymentMethodRowViewModel(
                    details = paymentMethodDetails,
                    select = { openEditPaymentMethod(paymentMethodDetails.id) }
                )
            }
        }

    init {
        viewModelScope.launch {
            repository.updatePaymentMethodDetails()
        }
    }

    fun delete(item: PaymentMethodRowViewModel) {
        viewModelScope.launch {
            repository.deletePaymentMethod(item.id)
        }
    }
}