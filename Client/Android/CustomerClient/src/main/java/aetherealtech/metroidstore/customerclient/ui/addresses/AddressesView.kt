package aetherealtech.metroidstore.customerclient.ui.addresses

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.Colors
import aetherealtech.metroidstore.customerclient.ui.addressrow.AddressRowView
import aetherealtech.metroidstore.customerclient.ui.addressrow.AddressRowViewModel
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.mapState
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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

class AddressesViewModel(
    private val repository: UserRepository,
    val openAddAddress: () -> Unit,
    val openEditAddress: (Address.ID) -> Unit
): ViewModel() {
    val items = repository.addressDetails
        .mapState { addressDetailsList ->
            addressDetailsList.map { addressDetails ->
                AddressRowViewModel(
                    details = addressDetails,
                    select = { openEditAddress(addressDetails.address.id) }
                )
            }
        }

    init {
        viewModelScope.launch {
            repository.updateAddressDetails()
        }
    }

    fun delete(item: AddressRowViewModel) {
        viewModelScope.launch {
            repository.deleteAddress(item.id)
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