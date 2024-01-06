package aetherealtech.metroidstore.customerclient.ui.editpaymentmethod

import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.metroidstore.customerclient.widgets.FormValue
import aetherealtech.metroidstore.customerclient.widgets.requiredNonEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditPaymentMethodViewModel private constructor(
    repository: UserRepository,
    name: String,
    number: PaymentMethodDetails.Number,
    isPrimary: Boolean,
    save: suspend (EditPaymentMethod) -> Unit,
    onSaveComplete: () -> Unit
): ViewModel() {
    val busy = repository.busy

    val name = FormValue.requiredNonEmpty(name)
    val number = FormValue.requiredNonEmpty(number.value)
    val isPrimary = MutableStateFlow(isPrimary)

    val create: StateFlow<(() -> Unit)?> = StateFlows.combine(
        this.name.value,
        this.number.value,
        this.isPrimary
    )
        .mapState { values ->
            val first = values.first
            val second = values.second

            if(
                first == null ||
                second == null
            )
                return@mapState null

            val paymentMethod = EditPaymentMethod(
                name = first,
                number = PaymentMethodDetails.Number(second),
                isPrimary = values.third
            )

            return@mapState {
                viewModelScope.launch {
                    save(paymentMethod)
                    onSaveComplete()
                }
            }
        }

    companion object {
        fun new(
            repository: UserRepository,
            onSaveComplete: () -> Unit
        ): EditPaymentMethodViewModel = EditPaymentMethodViewModel(
            repository = repository,
            name = "",
            number = PaymentMethodDetails.Number(""),
            isPrimary = false,
            save = { paymentMethod -> repository.createPaymentMethod(paymentMethod) },
            onSaveComplete = onSaveComplete
        )

        fun edit(
            id: PaymentMethodID,
            repository: UserRepository,
            onSaveComplete: () -> Unit
        ): EditPaymentMethodViewModel {
            val paymentMethod = repository.paymentMethodDetails.value
                .find { paymentMethod -> paymentMethod.id == id }

            if(paymentMethod == null)
                throw IllegalArgumentException("No payment method with that ID was found")

            return EditPaymentMethodViewModel(
                repository = repository,
                name = paymentMethod.name,
                number = paymentMethod.number,
                isPrimary = paymentMethod.isPrimary,
                save = { editPaymentMethod ->
                    repository.updatePaymentMethod(
                        editPaymentMethod,
                        paymentMethod.id
                    )
                },
                onSaveComplete = onSaveComplete
            )
        }
    }
}