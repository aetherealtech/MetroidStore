package aetherealtech.metroidstore.customerclient.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.ViewModel
import com.aetherealtech.metroidstore.customerclient.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class SettingsViewModel(
    openAddresses: () -> Unit,
    openPaymentMethods: () -> Unit,
    logout: () -> Unit
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
        Option(
            icon = { ImageVector.vectorResource(R.drawable.baseline_logout_24) },
            title = "Log Out",
            select = logout
        ),
    )
}