package aetherealtech.metroidstore.customerclient.model

data class NewAddress(
    val name: String,
    val street1: Address.Street1,
    val street2: Address.Street2?,
    val locality: Address.Locality,
    val province: Address.Province,
    val country: Address.Country,
    val planet: Address.Planet,
    val postalCode: Address.PostalCode?,
    val isPrimary: Boolean
)