package aetherealtech.metroidstore.customerclient.ui.productlistrow

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
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.datasources.fake.summary
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncImage
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.StarRatingView

@Composable
fun ProductRowView(
    modifier: Modifier = Modifier,
    viewModel: ProductRowViewModel
) {
    Box(
        modifier = modifier
            .clickable { viewModel.select() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                @Composable
                fun Description(text: String) {
                    Text(
                        text = text,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = viewModel.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Description(text = viewModel.type)
                Description(text = viewModel.game)
                if (viewModel.ratings != null) {
                    StarRatingView(viewModel = viewModel.ratings)
                }
                PriceView(viewModel = viewModel.price)
            }
        }
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun ProductRowPreview() {
    MetroidStoreTheme {
        ProductRowView(
            viewModel = ProductRowViewModel(
                product = DataSourceFake.fakeProducts[0].summary,
                select = { }
            )
        )
    }
}