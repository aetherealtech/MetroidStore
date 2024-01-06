package aetherealtech.metroidstore.customerclient.ui.placeorderbutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import aetherealtech.metroidstore.customerclient.ui.orderconfirmation.OrderConfirmationView
import aetherealtech.metroidstore.customerclient.widgets.PrimaryCallToAction

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

