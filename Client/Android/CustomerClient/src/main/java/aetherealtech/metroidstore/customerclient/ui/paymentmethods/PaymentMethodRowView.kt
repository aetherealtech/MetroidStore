package aetherealtech.metroidstore.customerclient.paymentmethods

import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

@Composable
fun PaymentMethodRowView(
    viewModel: PaymentMethodRowViewModel
) {
    Column(
        modifier = Modifier
            .background(Color(0xFFF8F8F8))
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
            .clickable(onClick = viewModel.select)
    ) {
        Text(
            text = viewModel.name,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = Color(0xFFEEEEEE)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFCCCCCC)
                    )
            ) {
                if (viewModel.isPrimary) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Primary",
                        tint = Color.Blue
                    )
                }
            }

            Text(
                text = "Primary Payment Method"
            )
        }
    }
}

class PaymentMethodRowViewModel(
    details: PaymentMethodDetails,
    val select: () -> Unit
): ViewModel() {
    val id = details.id
    val name = details.name
    val isPrimary = details.isPrimary
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodRowPreview() {
    MetroidStoreTheme {
        PaymentMethodRowView(
            viewModel = PaymentMethodRowViewModel(
                details = PaymentMethodDetails(
                    id = PaymentMethodID(0),
                    name = "Lair",
                    number = PaymentMethodDetails.Number("1234123412341234"),
                    isPrimary = true
                ),
                select = { }
            )
        )
    }
}