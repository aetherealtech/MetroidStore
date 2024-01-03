package aetherealtech.metroidstore.customerclient.ui.orderdetails

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.ui.orderitemrow.OrderItemRowView
import aetherealtech.metroidstore.customerclient.ui.orderactivity.OrderActivityView
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.CenterModal
import aetherealtech.metroidstore.customerclient.widgets.PriceView
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderDetailsView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: OrderDetailsViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Order #${viewModel.orderID.value}"
        ))
    }

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

@Preview(showBackground = true)
@Composable
fun OrderDetailsPreview() {
    MetroidStoreTheme {
        OrderDetailsView(
            setAppBarState = { },
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