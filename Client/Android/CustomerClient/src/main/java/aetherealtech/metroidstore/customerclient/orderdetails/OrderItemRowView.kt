package aetherealtech.metroidstore.customerclient.orderdetails

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.fakedatasources.orderItem
import aetherealtech.metroidstore.customerclient.model.ImageSource
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncImage
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

@Composable
fun OrderItemRowView(
    modifier: Modifier = Modifier,
    viewModel: OrderItemRowViewModel
) {
    Box(
        modifier = modifier
            .clickable { viewModel.select() }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier.height(128.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxHeight(),
                    source = viewModel.image,
                    contentDescription = "Product Image"
                )
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = viewModel.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    PriceView(viewModel = viewModel.price)
                }
            }
        }
        Divider()
    }
}

class OrderItemRowViewModel(
    item: OrderDetails.Item,
    val select: () -> Unit,
): ViewModel() {
    val image: ImageSource
    val name: String
    val price: PriceViewModel

    init {
        image = item.image
        name = "${item.name}${if (item.quantity > 1) " (${item.quantity})" else ""}"
        price = PriceViewModel(item.price)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderItemRowPreview() {
    MetroidStoreTheme {
        OrderItemRowView(
            viewModel = OrderItemRowViewModel(
                item = DataSourceFake.fakeProducts[0].orderItem(quantity = 3),
                select = { }
            )
        )
    }
}