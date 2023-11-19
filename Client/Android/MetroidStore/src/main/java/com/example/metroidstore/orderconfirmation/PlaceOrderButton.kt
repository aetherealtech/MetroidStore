package com.example.metroidstore.orderconfirmation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.model.NewOrder
import com.example.metroidstore.model.OrderID
import com.example.metroidstore.repositories.UserRepository
import com.example.metroidstore.uitoolkit.ConfirmationModal
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.widgets.BottomModal
import com.example.metroidstore.uitoolkit.PrimaryCallToAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Composable
fun PlaceOrderButton(
    viewModel: PlaceOrderViewModel
) {
    val placeOrder by viewModel.placeOrder.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth(0.75f)
    ) {
        PrimaryCallToAction(
            onClick = placeOrder,
            text = "Place Order"
        )
    }

    OrderConfirmationView(
        viewModel = viewModel.confirmationViewModel
    )
}

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