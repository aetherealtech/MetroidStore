package aetherealtech.metroidstore.customerclient.ui.orderconfirmation

import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.widgets.ConfirmationModalViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderConfirmationViewModel private constructor(
    private val _addedViewModel: MutableStateFlow<OrderConfirmationContentViewModel?>
): ConfirmationModalViewModel<OrderConfirmationContentViewModel>(
    _addedViewModel.asStateFlow()
) {
    constructor(
        order: NewOrder,
        userRepository: UserRepository,
        viewOrder: (OrderID) -> Unit
    ) : this(MutableStateFlow(null)) {
        viewModelScope.launch {
            val orderID = userRepository.placeOrder(order)

            _addedViewModel.value = OrderConfirmationContentViewModel(
                orderID = orderID,
                viewOrder = viewOrder
            )
        }
    }
}

class OrderConfirmationContentViewModel(
    private val orderID: OrderID,
    private val viewOrder: (OrderID) -> Unit
): ViewModel() {
    val text: String

    init {
        text = "Order Placed!"
    }

    fun viewOrder() {
        viewOrder(orderID)
    }
}