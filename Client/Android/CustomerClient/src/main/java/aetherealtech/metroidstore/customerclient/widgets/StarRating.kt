package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import aetherealtech.metroidstore.customerclient.model.Rating
import aetherealtech.metroidstore.customerclient.model.rating
import java.text.DecimalFormat

operator fun Color.div(other: Float): Color {
    return this.copy(
        red = red / other,
        green = green / other,
        blue = blue / other,
    )
}

@Composable
fun StarRatingView(
    viewModel: StarRatingViewModel
) {
    @Composable
    fun Star(filled: Boolean) {
        Icon(
            imageVector = if (filled) Icons.Filled.Star else Icons.TwoTone.Star,
            contentDescription = null,
            modifier = Modifier.height(16.dp),
            tint = Color.Yellow / 2.0f
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = viewModel.displayedRating,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light
        )
        (1..5).forEach { index ->
            Star(filled = viewModel.rating >= index)
        }
        Text(
            text = viewModel.count,
            fontSize = 10.sp
        )
    }
}

class StarRatingViewModel private constructor(
    ratingCount: Int,
    rating: Float
): ViewModel() {
    companion object {
        fun create(
            ratingCount: Int,
            rating: Float?
        ): StarRatingViewModel? {
            if(ratingCount == 0 || rating == null)
                return null

            return StarRatingViewModel(
                ratingCount,
                rating
            )
        }
    }

    private val format = DecimalFormat("0.0")

    val rating: Int
    val displayedRating: String
    val count: String

    init {
        this.rating = rating.toInt()
        this.displayedRating = format.format(rating)
        this.count = "(${ratingCount})"
    }
}