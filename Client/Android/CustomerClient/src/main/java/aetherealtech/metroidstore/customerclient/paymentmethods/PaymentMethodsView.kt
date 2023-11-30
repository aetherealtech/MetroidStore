package aetherealtech.metroidstore.customerclient.paymentmethods

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import aetherealtech.metroidstore.customerclient.widgets.SwipeToDeleteRow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

class PaymentMethodsViewModel(
    private val repository: UserRepository,
    val openAddPaymentMethod: () -> Unit,
    val openEditPaymentMethod: (PaymentMethodID) -> Unit
): ViewModel() {
    val items = repository.paymentMethodDetails
        .mapState { paymentMethodDetailsList ->
            paymentMethodDetailsList.map { paymentMethodDetails ->
                PaymentMethodRowViewModel(
                    details = paymentMethodDetails,
                    select = { openEditPaymentMethod(paymentMethodDetails.id) }
                )
            }
        }

    init {
        viewModelScope.launch {
            repository.updatePaymentMethodDetails()
        }
    }

    fun delete(item: PaymentMethodRowViewModel) {
        viewModelScope.launch {
            repository.deletePaymentMethod(item.id)
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