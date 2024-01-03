package aetherealtech.metroidstore.customerclient.ui.addressrow

import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

@Composable
fun AddressRowView(
    viewModel: AddressRowViewModel
) {
    Column(
        modifier = Modifier
            .background(Color(0xFFF8F8F8))
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
            .clickable(onClick = viewModel.select)
    ) {
        Text(
            text = viewModel.name,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Text(
            text = viewModel.address
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = Color(0xFFEEEEEE)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFCCCCCC)
                    )
            ) {
                if (viewModel.isPrimary) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Primary",
                        tint = Color.Blue
                    )
                }
            }

            Text(
                text = "Primary Address"
            )
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun AddressRowPreview() {
    MetroidStoreTheme {
        AddressRowView(
            viewModel = AddressRowViewModel(
                details = UserAddressDetails(
                    name = "Lair",
                    address = Address(
                        id = Address.ID(0),
                        street1 = Address.Street1("123 Fake St."),
                        street2 = Address.Street2("#4"),
                        locality = Address.Locality("Fakerton"),
                        province = Address.Province("CA"),
                        country = Address.Country("USA"),
                        planet = Address.Planet("Earth"),
                        postalCode = Address.PostalCode("90210")
                    ),
                    isPrimary = true
                ),
                select = { }
            )
        )
    }
}