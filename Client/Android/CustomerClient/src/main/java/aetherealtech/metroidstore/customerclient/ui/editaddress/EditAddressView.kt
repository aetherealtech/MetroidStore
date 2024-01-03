package aetherealtech.metroidstore.customerclient.ui.editaddress

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
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
fun EditAddressView(
    setAppBarState: (AppBarState) -> Unit,
    viewModel: EditAddressViewModel
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
                text = "Save"
            )
        }
    }
}

class EditAddressViewModel private constructor(
    repository: UserRepository,
    name: String,
    street1: Address.Street1,
    street2: Address.Street2?,
    locality: Address.Locality,
    province: Address.Province,
    country: Address.Country,
    planet: Address.Planet,
    postalCode: Address.PostalCode?,
    isPrimary: Boolean,
    save: suspend (EditAddress) -> Unit,
    onSaveComplete: () -> Unit
): ViewModel() {
    val busy = repository.busy

    val name = FormValue.requiredNonEmpty(name)
    val street1 = FormValue.requiredNonEmpty(street1.value)
    val street2 = FormValue.optionalNonEmpty(street2?.value)
    val locality = FormValue.requiredNonEmpty(locality.value)
    val province = FormValue.requiredNonEmpty(province.value)
    val country = FormValue.requiredNonEmpty(country.value)
    val planet = FormValue.requiredNonEmpty(planet.value)
    val postalCode = FormValue.optionalNonEmpty(postalCode?.value)
    val isPrimary = MutableStateFlow(isPrimary)

    val create: StateFlow<(() -> Unit)?> = StateFlows
        .combine(
            this.name.value,
            this.street1.value,
            this.street2.value,
            this.locality.value,
            this.province.value,
            this.country.value,
            this.planet.value,
            this.postalCode.value,
            this.isPrimary
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

            val address = EditAddress(
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
                    save(address)
                    onSaveComplete()
                }
            }
        }

    companion object {
        fun new(
            repository: UserRepository,
            onSaveComplete: () -> Unit
        ): EditAddressViewModel = EditAddressViewModel(
            repository = repository,
            name = "",
            street1 = Address.Street1(""),
            street2 = null,
            locality = Address.Locality(""),
            province = Address.Province(""),
            country = Address.Country(""),
            planet = Address.Planet(""),
            postalCode = null,
            isPrimary = false,
            save = { address -> repository.createAddress(address) },
            onSaveComplete = onSaveComplete
        )

        fun edit(
            id: Address.ID,
            repository: UserRepository,
            onSaveComplete: () -> Unit
        ): EditAddressViewModel {
            val address = repository.addressDetails.value
                .find { address -> address.address.id == id }

            if(address == null)
                throw IllegalArgumentException("No address with that ID was found")

            return EditAddressViewModel(
                repository = repository,
                name = address.name,
                street1 = address.address.street1,
                street2 = address.address.street2,
                locality = address.address.locality,
                province = address.address.province,
                country = address.address.country,
                planet = address.address.planet,
                postalCode = address.address.postalCode,
                isPrimary = address.isPrimary,
                save = { editAddress -> repository.updateAddress(editAddress, address.address.id) },
                onSaveComplete = onSaveComplete
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddAddressPreview() {
    MetroidStoreTheme {
        EditAddressView(
            setAppBarState = { },
            viewModel = EditAddressViewModel.new(
                repository = UserRepository(
                    dataSource = DataSourceFake().user,
                ),
                onSaveComplete = { }
            )
        )
    }
}