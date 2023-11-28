package aetherealtech.metroidstore.customerclient.addaddress

import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.NewAddress
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
import aetherealtech.metroidstore.customerclient.widgets.optionalNonEmpty
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
fun AddAddressView(
    setAppBarState: (AppBarState) -> Unit,
    viewModel: AddAddressViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Add Address"
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
                    name = "Street",
                    value = viewModel.street1
                )
                LabeledValidatedTextField(
                    name = "Street (Line 2)",
                    value = viewModel.street2
                )
                LabeledValidatedTextField(
                    name = "Locality",
                    value = viewModel.locality
                )
                LabeledValidatedTextField(
                    name = "Province",
                    value = viewModel.province
                )
                LabeledValidatedTextField(
                    name = "Country",
                    value = viewModel.country
                )
                LabeledValidatedTextField(
                    name = "Planet",
                    value = viewModel.planet
                )
                LabeledValidatedTextField(
                    name = "Postal Code",
                    value = viewModel.postalCode
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
                text = "Create"
            )
        }
    }
}

class AddAddressViewModel(
    repository: UserRepository
): ViewModel() {
    val busy = repository.busy

    val name = FormValue.requiredNonEmpty("")
    val street1 = FormValue.requiredNonEmpty("")
    val street2 = FormValue.optionalNonEmpty(null)
    val locality = FormValue.requiredNonEmpty("")
    val province = FormValue.requiredNonEmpty("")
    val country = FormValue.requiredNonEmpty("")
    val planet = FormValue.requiredNonEmpty("")
    val postalCode = FormValue.optionalNonEmpty(null)
    val isPrimary = MutableStateFlow(false)

    val create: StateFlow<(() -> Unit)?> = StateFlows
        .combine(
            name.value,
            street1.value,
            street2.value,
            locality.value,
            province.value,
            country.value,
            planet.value,
            postalCode.value,
            isPrimary
        )
        .mapState { values ->
            if(
                values.first == null ||
                values.second == null ||
                values.fourth == null ||
                values.fifth == null ||
                values.sixth == null ||
                values.seventh == null
            )
                return@mapState null

            val address = NewAddress(
                name = values.first,
                street1 = Address.Street1(values.second),
                street2 = values.third?.let { value -> Address.Street2(value) },
                locality = Address.Locality(values.fourth),
                province = Address.Province(values.fifth),
                country = Address.Country(values.sixth),
                planet = Address.Planet(values.seventh),
                postalCode = values.eighth?.let { value -> Address.PostalCode(value) },
                isPrimary = values.ninth
            )

            return@mapState {
                viewModelScope.launch {
                    repository.createAddress(address)
                }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun AddAddressPreview() {
    MetroidStoreTheme {
        AddAddressView(
            setAppBarState = { },
            viewModel = AddAddressViewModel(
                repository = UserRepository(
                    dataSource = DataSourceFake().user
                )
            )
        )
    }
}