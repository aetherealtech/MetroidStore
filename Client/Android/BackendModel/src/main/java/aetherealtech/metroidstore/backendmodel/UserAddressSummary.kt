package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class UserAddressSummary(
    val addressID: Int,
    val name: String,
    val isPrimary: Boolean
)