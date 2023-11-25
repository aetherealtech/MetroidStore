package aetherealtech.metroidstore.customerclient.orderdetails

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.orderactivity.OrderActivityView
import aetherealtech.metroidstore.customerclient.orderactivity.OrderActivityViewModel
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.displayString
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.CenterModal
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun OrderDetailsView(
    modifier: Modifier = Modifier,
    viewModel: OrderDetailsViewModel
) {
    AsyncLoadedShimmering(
        modifier = modifier,
        data = viewModel.content
    ) { modifier, contentViewModel ->
        OrderDetailsContentView(
            modifier = modifier,
            viewModel = contentViewModel
        )
    }
}

@Composable
fun OrderDetailsContentView(
    modifier: Modifier = Modifier,
    viewModel: OrderDetailsContentViewModel
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        item {
            OrderDetailsSummaryView(
                viewModel = viewModel.summary
            )
        }

        items(viewModel.items) { rowViewModel ->
            OrderItemRowView(
                viewModel = rowViewModel
            )
        }
    }
}

@Composable
fun OrderDetailsSummaryView(
    modifier: Modifier = Modifier,
    viewModel: OrderDetailsSummaryViewModel
) {
    Column {
        Column(
            modifier = modifier
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = viewModel.date,
                fontSize = 24.sp
            )
            Text(
                text = viewModel.status,
                fontSize = 20.sp
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        fontSize = 18.sp
                    )
                    PriceView(
                        viewModel = viewModel.total
                    )
                }

                Text(text = viewModel.address)
                Text(text = viewModel.shippingMethod)
                Text(text = viewModel.paymentMethod)
            }

            Spacer(
                modifier = Modifier.weight(1.0f)
            )

            Text(
                modifier = Modifier
                    .clickable { viewModel.viewActivity() },
                text = "View Updates",
                color = Color.Blue
            )
        }
    }

    CenterModal(
        data = viewModel.activity
    ) { activityViewModel, _ ->
        OrderActivityView(
            viewModel = activityViewModel
        )
    }
}

class OrderDetailsViewModel(
    private val orderID: OrderID,
    private val repository: OrderRepository,
    selectItem: (ProductID) -> Unit
): ViewModel() {
    private val _content = MutableStateFlow<OrderDetailsContentViewModel?>(null)

    val content = _content
        .asStateFlow()

    init {
        viewModelScope.launch {
            val orderDetails = repository.getOrder(orderID)

            _content.value = OrderDetailsContentViewModel(
                orderDetails = orderDetails,
                selectItem = selectItem,
                activityViewModelFactory = {
                    OrderActivityViewModel(
                        orderID = orderID,
                        repository = repository
                    )
                }
            )
        }
    }
}

class OrderDetailsContentViewModel(
    orderDetails: OrderDetails,
    selectItem: (ProductID) -> Unit,
    activityViewModelFactory: () -> OrderActivityViewModel
): ViewModel() {
    val summary = OrderDetailsSummaryViewModel(
        orderDetails = orderDetails,
        activityViewModelFactory = activityViewModelFactory
    )

    val items = orderDetails.items
        .map { item ->
            OrderItemRowViewModel(
                item = item,
                select = { selectItem(item.productID) }
            )
        }
        .toImmutableList()
}

class OrderDetailsSummaryViewModel(
    orderDetails: OrderDetails,
    private val activityViewModelFactory: () -> OrderActivityViewModel
): ViewModel() {
    private val _activity = MutableSharedFlow<OrderActivityViewModel>()

    val date: String
    val status: String
    val total: PriceViewModel
    val address: String
    val shippingMethod: String
    val paymentMethod: String

    val activity = _activity
        .asSharedFlow()

    init {
        date = orderDetails.date.displayString
        status = orderDetails.latestStatus.value

        total = PriceViewModel(orderDetails.total)

        address = "Deliver to: ${orderDetails.address}"
        shippingMethod = "Shipping Method: ${orderDetails.shippingMethod}"
        paymentMethod = "Payment Method: ${orderDetails.paymentMethod}"
    }

    fun viewActivity() {
        viewModelScope.launch {
            _activity.emit(activityViewModelFactory())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsPreview() {
    MetroidStoreTheme {
        OrderDetailsView(
            viewModel = OrderDetailsViewModel(
                orderID = OrderID(1),
                repository = OrderRepository(
                    dataSource = DataSourceFake().orders
                ),
                selectItem = { }
            )
        )
    }
}