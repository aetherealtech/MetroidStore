package aetherealtech.metroidstore.customerclient.ui.paymentmethods

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.Colors
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.ui.paymentmethodrow.PaymentMethodRowView
import aetherealtech.androiduitoolkit.AsyncLoadedShimmering
import aetherealtech.androiduitoolkit.SwipeToDeleteRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PaymentMethodsView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: PaymentMethodsViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "PaymentMethods",
            actions = {
                IconButton(
                    onClick = viewModel.openAddPaymentMethod
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        tint = Colors.BarForeground,
                        contentDescription = null
                    )
                }
            }
        ))
    }

    AsyncLoadedShimmering(
        modifier = modifier,
        data = viewModel.items
    ) { modifier, items ->
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = items,
                key = { item -> item.id.value }
            ) { rowViewModel ->
                SwipeToDeleteRow(
                    onDelete = { viewModel.delete(rowViewModel) }
                ) {
                    PaymentMethodRowView(
                        viewModel = rowViewModel
                    )
                }
                
                Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodsPreview() {
    MetroidStoreTheme {
        PaymentMethodsView(
            setAppBarState = { },
            viewModel = PaymentMethodsViewModel(
                repository = UserRepository(
                    dataSource = DataSourceFake().user
                ),
                openAddPaymentMethod = { },
                openEditPaymentMethod = { }
            )
        )
    }
}