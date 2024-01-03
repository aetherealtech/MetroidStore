package aetherealtech.metroidstore.customerclient.uitoolkit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import aetherealtech.metroidstore.customerclient.theme.Colors

@Composable
fun PrimaryCallToAction(
    modifier: Modifier = Modifier,
    viewModel: PrimaryCallToActionViewModel
) {
    Button(
        onClick = { viewModel.onClick() },
        modifier = modifier
            .fillMaxWidth(),
        enabled = viewModel.enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Colors.PrimaryCallToAction
        )
    ) {
        Text(text = viewModel.text)
    }
}

@Composable
fun PrimaryCallToAction(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    text: String
) {
    PrimaryCallToAction(
        modifier = modifier,
        viewModel = PrimaryCallToActionViewModel(
            action = onClick,
            text = text
        )
    )
}

class PrimaryCallToActionViewModel(
    private val action: (() -> Unit)?,
    val text: String
) : ViewModel() {
    val enabled = action != null

    fun onClick() {
        action?.invoke()
    }
}