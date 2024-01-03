package aetherealtech.metroidstore.customerclient.ui.paymentmethodrow

import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import androidx.lifecycle.ViewModel

class PaymentMethodRowViewModel(
    details: PaymentMethodDetails,
    val select: () -> Unit
): ViewModel() {
    val id = details.id
    val name = details.name
    val isPrimary = details.isPrimary
}