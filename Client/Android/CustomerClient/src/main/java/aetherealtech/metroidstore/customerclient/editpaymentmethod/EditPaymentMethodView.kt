package aetherealtech.metroidstore.customerclient.editpaymentmethod

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import aetherealtech.metroidstore.customerclient.utilities.StateFlows
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.BusyView
import aetherealtech.metroidstore.customerclient.widgets.FormValue
import aetherealtech.metroidstore.customerclient.widgets.LabeledSwitch
import aetherealtech.metroidstore.customerclient.widgets.LabeledValidatedTextField
import aetherealtech.metroidstore.customerclient.widgets.requiredNonEmpty
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun EditPaymentMethodView(
    setAppBarState: (AppBarState) -> Unit,
    viewModel: EditPaymentMethodViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Add PaymentMethod"
        ))
    }

    val create by viewModel.create.collectAsState()

    BusyView(
        busy = viewModel.busy
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledValidatedTextField(
                    name = "Name",
                    value = viewModel.name
                )
                LabeledValidatedTextField(
                    name = "Number",
                    value = viewModel.number
                )

                LabeledSwitch(
                    name = "Primary",
                    value = viewModel.isPrimary
                )
            }

            PrimaryCallToAction(
                modifier = Modifier
                    .width(128.dp),
                onClick = create,
                text = "Save"
            )
        }
    }
}

class EditPaymentMethodViewModel private constructor(
    repository: UserRepository,
    name: String,
    number: PaymentMethodDetails.Number,
    isPrimary: Boolean,
    save: suspend (EditPaymentMethod) -> Unit,
    onSaveComplete: () -> Unit
): ViewModel() {
    val busy = repository.busy

    val name = FormValue.requiredNonEmpty(name)
    val number = FormValue.requiredNonEmpty(number.value)
    val isPrimary = MutableStateFlow(isPrimary)

    val create: StateFlow<(() -> Unit)?> = StateFlows
        .combine(
            this.name.value,
            this.number.value,
            this.isPrimary
        )
        .mapState { values ->
            val first = values.first
            val second = values.second

            if(
                first == null ||
                second == null
            )
                return@mapState null

            val paymentMethod = EditPaymentMethod(
                name = first,
                number = PaymentMethodDetails.Number(second),
                isPrimary = values.third
            )

            return@mapState {
                viewModelScope.launch {
                    save(paymentMethod)
                    onSaveComplete()
                }
            }
        }

    companion object {
        fun new(
            repository: UserRepository,
            onSaveComplete: () -> Unit
        ): EditPaymentMethodViewModel = EditPaymentMethodViewModel(
            repository = repository,
            name = "",
            number = PaymentMethodDetails.Number(""),
            isPrimary = false,
            save = { paymentMethod -> repository.createPaymentMethod(paymentMethod) },
            onSaveComplete = onSaveComplete
        )

        fun edit(
            id: PaymentMethodID,
            repository: UserRepository,
            onSaveComplete: () -> Unit
        ): EditPaymentMethodViewModel {
            val paymentMethod = repository.paymentMethodDetails.value
                .find { paymentMethod -> paymentMethod.id == id }

            if(paymentMethod == null)
                throw IllegalArgumentException("No payment method with that ID was found")

            return EditPaymentMethodViewModel(
                repository = repository,
                name = paymentMethod.name,
                number = paymentMethod.number,
                isPrimary = paymentMethod.isPrimary,
                save = { editPaymentMethod -> repository.updatePaymentMethod(editPaymentMethod, paymentMethod.id) },
                onSaveComplete = onSaveComplete
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPaymentMethodPreview() {
    MetroidStoreTheme {
        EditPaymentMethodView(
            setAppBarState = { },
            viewModel = EditPaymentMethodViewModel.new(
                repository = UserRepository(
                    dataSource = DataSourceFake().user,
                ),
                onSaveComplete = { }
            )
        )
    }
}