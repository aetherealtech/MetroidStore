package aetherealtech.metroidstore.backendmodel

import kotlinx.serialization.Serializable

@Serializable
data class OrderActivity(
    val status: String,
    val date: String
)