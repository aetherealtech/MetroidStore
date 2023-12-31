package aetherealtech.metroidstore.customerclient.model

data class UserAddressSummary(
    val addressID: Address.ID,
    val name: String,
    val isPrimary: Boolean
)