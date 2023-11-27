package aetherealtech.metroidstore.customerclient.addresses

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                .padding(horizontal = 16.dp)
        ) {
            items(items) { rowViewModel ->
                AddressRowView(
                    viewModel = rowViewModel
                )
            }
        }
    }
}

class AddressesViewModel(
    repository: UserRepository,
    val openAddAddress: () -> Unit
): ViewModel() {
    val items = repository.addressDetails
        .mapState { addressDetailsList ->
            addressDetailsList.map { addressDetails ->
                AddressRowViewModel(
                    details = addressDetails
                )
            }
        }

    init {
        viewModelScope.launch {
            repository.updateAddressDetails()
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
                openAddAddress = { }
            )
        )
    }
}