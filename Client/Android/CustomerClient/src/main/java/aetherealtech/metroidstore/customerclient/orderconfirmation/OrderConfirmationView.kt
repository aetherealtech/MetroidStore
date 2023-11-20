package aetherealtech.metroidstore.customerclient.orderconfirmation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.uitoolkit.ConfirmationModal
import aetherealtech.metroidstore.customerclient.uitoolkit.ConfirmationModalViewModel
import aetherealtech.metroidstore.customerclient.widgets.CloseableView
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun OrderConfirmationView(
    viewModel: Flow<OrderConfirmationViewModel>
) {
    ConfirmationModal(
        viewModel = viewModel
    ) { contentViewModel, onClose ->
        OrderConfirmationContentView(
            viewModel = contentViewModel,
            onClose = onClose
        )
    }
}

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

@Composable
fun OrderConfirmationContentView(
    viewModel: OrderConfirmationContentViewModel,
    onClose: () -> Unit,
) {
    CloseableView(onClose = onClose) {
        Column(
            modifier = Modifier.height(256.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.text
            )
            Box(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                PrimaryCallToAction(
                    onClick = { viewModel.viewOrder() },
                    text = "View Order"
                )
            }
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

@Preview(showBackground = true)
@Composable
fun OrderConfirmationContentPreview() {
    MetroidStoreTheme {
        OrderConfirmationContentView(
            viewModel = OrderConfirmationContentViewModel(
                orderID = OrderID(0),
                viewOrder = { }
            ),
            onClose = { }
        )
    }
}