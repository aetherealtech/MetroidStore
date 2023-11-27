package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class UserAddressDetails(
    val name: String,
    val addressID: Int,
    val street1: String,
    val street2: String?,
    val locality: String,
    val province: String,
    val country: String,
    val planet: String,
    val postalCode: String?,
    val isPrimary: Boolean
)