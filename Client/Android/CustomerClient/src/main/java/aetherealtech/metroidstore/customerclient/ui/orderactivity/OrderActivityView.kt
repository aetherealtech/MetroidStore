package aetherealtech.metroidstore.customerclient.orderactivity

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.OrderActivity
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.displayString
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.MilestoneProgressView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

class OrderActivityViewModel(
    orderID: OrderID,
    repository: OrderRepository
): ViewModel() {
    private val _content = MutableStateFlow<OrderActivityContentViewModel?>(null)

    val content = _content
        .asStateFlow()

    init {
        viewModelScope.launch {
            val activities = repository.getOrderActivity(orderID)

            _content.value = OrderActivityContentViewModel(
                activities = activities
            )
        }
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

class OrderActivityContentViewModel(
    activities: ImmutableList<OrderActivity>
): ViewModel() {
    val currentStatus: String
    val currentStateDate: String

    val statuses: ImmutableList<String>
    val activeStatusIndex: Int

    init {
        val currentActivity = activities.last()

        currentStatus = currentActivity.status.value
        currentStateDate = currentActivity.date.displayString

        val statuses = activities
            .map { activity -> activity.status }
            .toMutableList()

        if(!statuses.contains(OrderStatus.SHIPPED))
            statuses.add(OrderStatus.SHIPPED)

        if(!statuses.contains(OrderStatus.DELIVERED) &&
            !statuses.contains(OrderStatus.CANCELLED)
        )
            statuses.add(OrderStatus.DELIVERED)

        this.statuses = statuses
            .map { status -> status.value }
            .toImmutableList()

        activeStatusIndex = this.statuses
            .indexOf(currentStatus)
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