package aetherealtech.metroidstore.customerclient.ui.addresses

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.Colors
import aetherealtech.metroidstore.customerclient.ui.addressrow.AddressRowView
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.SwipeToDeleteRow
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
fun AddressesView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: AddressesViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Addresses",
            actions = {
                IconButton(
                    onClick = viewModel.openAddAddress
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
                    AddressRowView(
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
fun AddressesPreview() {
    MetroidStoreTheme {
        AddressesView(
            setAppBarState = { },
            viewModel = AddressesViewModel(
                repository = UserRepository(
                    dataSource = DataSourceFake().user
                ),
                openAddAddress = { },
                openEditAddress = { }
            )
        )
    }
}