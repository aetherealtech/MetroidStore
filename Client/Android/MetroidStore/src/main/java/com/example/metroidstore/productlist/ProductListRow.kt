package com.example.metroidstore.productlist

import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.metroidstore.model.Product
import com.example.metroidstore.model.Rating
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.StarRatingView
import kotlinx.collections.immutable.ImmutableList
import okhttp3.HttpUrl
import java.math.BigDecimal

@Composable
fun ProductListRow(
    viewModel: ProductRowViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(128.dp)
    ) {
        AsyncImage(
            model = HttpUrl.Builder()
                .scheme("https")
                .host("html.com")
                .addPathSegment("wp-content")
                .addPathSegment("uploads")
                .addPathSegment("flamingo.jpg")
                .build(),
            modifier = Modifier
                .aspectRatio(1.0f)
                .fillMaxHeight(),
            onSuccess = {
                println("SUCCESS")
            },
            onError = { error ->
                println("ERROR: $error")
            },
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

            Description(text = viewModel.name)
            Description(text = viewModel.type)
            Description(text = viewModel.game)
            StarRatingView(ratings = viewModel.ratings)
            PriceView(price = viewModel.price)
        }
    }
    Divider()
}

class ProductRowViewModel(
    product: Product
): ViewModel() {
    val name: String
    val type: String
    val game: String
    val ratings: ImmutableList<Rating>
    val price: BigDecimal

    init {
        name = product.name
        type = product.type
        game = product.game
        ratings = product.ratings
        price = product.price
    }
}