package aetherealtech.metroidstore.customerclient.ui.placeorderbutton

import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.ui.orderconfirmation.OrderConfirmationViewModel
import aetherealtech.kotlinflowsextensions.mapState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PlaceOrderViewModel(
    private val order: StateFlow<NewOrder?>,
    private val userRepository: UserRepository,
    private val viewOrder: (OrderID) -> Unit
): ViewModel() {
    val _confirmationViewModel = MutableSharedFlow<OrderConfirmationViewModel>()

    val confirmationViewModel = _confirmationViewModel
        .asSharedFlow()

    val placeOrder: StateFlow<(() -> Unit)?> = order
        .mapState { order ->
            if(order == null)
                return@mapState null

            return@mapState {
                viewModelScope.launch {
                    _confirmationViewModel.emit(
                        OrderConfirmationViewModel(
                            order = order,
                            userRepository = userRepository,
                            viewOrder = viewOrder
                        )
                    )
                }
            }
        }
}