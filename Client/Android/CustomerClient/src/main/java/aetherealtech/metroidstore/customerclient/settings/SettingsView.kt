package aetherealtech.metroidstore.customerclient.settings

import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.aetherealtech.metroidstore.customerclient.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SettingsView(
    setAppBarState: (AppBarState) -> Unit,
    viewModel: SettingsViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Settings"
        ))
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(viewModel.options) { option ->
            Box(
                modifier = Modifier
                    .aspectRatio(1.0f)
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(
                            size = 32.dp
                        )
                    )
                    .clickable(
                        onClick = option.select
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = option.icon(),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = option.title
                    )
                }
            }
        }
    }
}

class SettingsViewModel(
    openAddresses: () -> Unit,
    openPaymentMethods: () -> Unit,
): ViewModel() {
    data class Option(
        val icon: @Composable () -> ImageVector,
        val title: String,
        val select: () -> Unit
    )

    val options: ImmutableList<Option> = persistentListOf(
        Option(
            icon = { ImageVector.vectorResource(R.drawable.baseline_home_work_24) },
            title = "Addresses",
            select = openAddresses
        ),
        Option(
            icon = { ImageVector.vectorResource(R.drawable.baseline_payment_24) },
            title = "Payment Methods",
            select = openPaymentMethods
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MetroidStoreTheme {
        SettingsView(
            setAppBarState = { },
            viewModel = SettingsViewModel(
                openAddresses = { },
                openPaymentMethods = { }
            ),
        )
    }
}