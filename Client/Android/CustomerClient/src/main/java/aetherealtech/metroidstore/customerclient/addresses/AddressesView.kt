package aetherealtech.metroidstore.customerclient.addresses

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
        ) {
            items(
                items = items,
                key = { item -> item.id.value }
            ) { rowViewModel ->
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        viewModel.delete(rowViewModel)
                        return@rememberDismissState false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier
                        .animateItemPlacement(),
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        val color by animateColorAsState(Color.Red)
                        val scale by animateFloatAsState(1f)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Localized description",
                                modifier = Modifier
                                    .scale(scale)
                            )
                        }
                    },
                    dismissContent = {
                        AddressRowView(
                            viewModel = rowViewModel
                        )
                    }
                )

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