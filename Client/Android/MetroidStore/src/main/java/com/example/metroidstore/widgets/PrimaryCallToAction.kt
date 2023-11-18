package com.example.metroidstore.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.metroidstore.ui.theme.Colors

@Composable
fun PrimaryCallToAction(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    text: String
) {
    Button(
        onClick = { onClick?.invoke() },
        modifier = modifier
            .fillMaxWidth(),
        enabled = onClick != null,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Colors.PrimaryCallToAction
        )
    ) {
        Text(text = text)
    }
}