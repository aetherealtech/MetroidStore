package com.example.metroidstore.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityControl(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    @Composable
    fun AdjustButton(
        onClick: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White, Color.hsv(hue = 0.0f, saturation = 0.0f, value = 0.85f))
                    )
                )
                .fillMaxHeight()
                .aspectRatio(1.0f),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            content()
        }
    }

    @Composable
    fun AdjustIcon(
        icon: ImageVector
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.6f)
        )
    }

    @Composable
    fun AdjustText(
        text: String
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Row {
        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White)
                .height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.width(128.dp)
            ) {
                AdjustButton(onClick = onDecrement) {
                    if (quantity == 1)
                        AdjustIcon(Icons.Outlined.Delete)
                    else
                        AdjustText("-")
                }
                Text(
                    text = "$quantity",
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                )
                AdjustButton(onClick = onIncrement) {
                    AdjustText("+")
                }
            }
        }
    }
}