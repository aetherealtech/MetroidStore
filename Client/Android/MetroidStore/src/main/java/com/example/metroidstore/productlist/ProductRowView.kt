package com.example.metroidstore.productlist

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.metroidstore.model.ImageSource
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.widgets.AsyncImage
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import com.example.metroidstore.widgets.StarRatingView
import com.example.metroidstore.widgets.StarRatingViewModel

@Composable
fun ProductRowView(
    modifier: Modifier = Modifier,
    viewModel: ProductRowViewModel
) {
    Box(
        modifier = modifier
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

class ProductRowViewModel(
    product: ProductSummary
): ViewModel() {
    val id: ProductID
    val image: ImageSource
    val name: String
    val type: String
    val game: String
    val ratings: StarRatingViewModel?
    val price: PriceViewModel

    init {
        id = product.id
        image = product.image
        name = product.name
        type = product.type
        game = product.game
        ratings = StarRatingViewModel.create(product.ratings)
        price = PriceViewModel(product.price)
    }
}