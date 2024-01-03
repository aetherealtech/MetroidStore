package aetherealtech.metroidstore.customerclient.ui.orderactivity

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.MilestoneProgressView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderActivityView(
    viewModel: OrderActivityViewModel
) {
    AsyncLoadedShimmering(
        data = viewModel.content
    ) { _, contentViewModel ->
        OrderActivityContentView(
            viewModel = contentViewModel
        )
    }
}

@Composable
fun OrderActivityContentView(
    viewModel: OrderActivityContentViewModel
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        )
    ) {
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Text(
                        text = viewModel.currentStatus,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " on",
                        fontSize = 24.sp,
                    )
                }
                Text(
                    text = viewModel.currentStateDate,
                    fontSize = 24.sp,
                )
            }
            MilestoneProgressView(
                milestones = viewModel.statuses,
                activeMilestoneIndex = viewModel.activeStatusIndex
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderActivityPreview() {
    MetroidStoreTheme {
        OrderActivityView(
            viewModel = OrderActivityViewModel(
                orderID = OrderID(0),
                repository = OrderRepository(
                    dataSource = DataSourceFake().orders
                )
            )
        )
    }
}