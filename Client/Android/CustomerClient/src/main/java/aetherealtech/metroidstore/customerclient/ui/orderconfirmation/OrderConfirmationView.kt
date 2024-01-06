package aetherealtech.metroidstore.customerclient.ui.orderconfirmation

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
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.ConfirmationModal
import aetherealtech.androiduitoolkit.CloseableView
import aetherealtech.metroidstore.customerclient.widgets.PrimaryCallToAction
import kotlinx.coroutines.flow.Flow

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