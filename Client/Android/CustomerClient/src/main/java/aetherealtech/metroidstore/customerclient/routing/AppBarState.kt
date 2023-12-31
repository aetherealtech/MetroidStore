package aetherealtech.metroidstore.customerclient.routing

import aetherealtech.metroidstore.customerclient.theme.Colors
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class AppBarState(
    val title: @Composable () -> Unit,
    val actions: @Composable RowScope.() -> Unit = {}
) {
    companion object {
        val default: AppBarState = AppBarState(
            title = "Metroid Store"
        )
    }

    constructor(
        title: String,
        actions: @Composable RowScope.() -> Unit = {}
    ) : this(
        title = {
            Text(
                text = title,
                color = Colors.BarForeground
            )
        },
        actions = actions
    )
}