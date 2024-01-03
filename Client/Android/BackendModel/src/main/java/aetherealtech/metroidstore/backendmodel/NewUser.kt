package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    val username: String,
    val password: String
)