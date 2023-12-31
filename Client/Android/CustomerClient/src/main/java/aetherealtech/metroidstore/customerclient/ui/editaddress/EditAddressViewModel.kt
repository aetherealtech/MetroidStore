package aetherealtech.metroidstore.customerclient.ui.editaddress

import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.androiduitoolkit.FormValue
import aetherealtech.androiduitoolkit.optionalNonEmpty
import aetherealtech.androiduitoolkit.requiredNonEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    val create: StateFlow<(() -> Unit)?> = StateFlows.combine(
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
            val first = values.first
            val second = values.second
            val fourth = values.fourth
            val fifth = values.fifth
            val sixth = values.sixth
            val seventh = values.seventh

            if(
                first == null ||
                second == null ||
                fourth == null ||
                fifth == null ||
                sixth == null ||
                seventh == null
            )
                return@mapState null

            val address = EditAddress(
                name = first,
                street1 = Address.Street1(second),
                street2 = values.third?.let { value -> Address.Street2(value) },
                locality = Address.Locality(fourth),
                province = Address.Province(fifth),
                country = Address.Country(sixth),
                planet = Address.Planet(seventh),
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