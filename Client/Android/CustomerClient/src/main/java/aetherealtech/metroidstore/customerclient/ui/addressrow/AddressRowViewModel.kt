package aetherealtech.metroidstore.customerclient.ui.addressrow

import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import androidx.lifecycle.ViewModel

class AddressRowViewModel(
    details: UserAddressDetails,
    val select: () -> Unit
): ViewModel() {
    val id = details.address.id

    val name: String

    val address: String

    val isPrimary: Boolean

    init {
        name = details.name

        val addressLines = mutableListOf<String>()

        addressLines.add(details.address.street1.value)

        details.address.street2?.let { street2 ->
            addressLines.add(street2.value)
        }

        val locationElements = listOf(
            details.address.locality.value,
            details.address.province.value,
            details.address.postalCode?.value
        )
            .filterNotNull()

        addressLines.add(locationElements.joinToString(", "))

        addressLines.add(listOf(
            details.address.country.value,
            details.address.planet.value
        ).joinToString(", "))

        address = addressLines
            .joinToString("\n")

        isPrimary = details.isPrimary
    }
}