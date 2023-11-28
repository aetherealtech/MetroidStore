package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class NewAddress(
    val name: String,
    val street1: String,
    val street2: String?,
    val locality: String,
    val province: String,
    val country: String,
    val planet: String,
    val postalCode: String?,
    val isPrimary: Boolean
)