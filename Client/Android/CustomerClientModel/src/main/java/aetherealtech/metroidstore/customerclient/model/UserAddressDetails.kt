package aetherealtech.metroidstore.customerclient.model

data class UserAddressDetails(
    val name: String,
    val address: Address,
    val isPrimary: Boolean
)