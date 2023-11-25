package aetherealtech.metroidstore.customerclient.model

import kotlinx.datetime.Instant

data class OrderActivity(
    val status: OrderStatus,
    val date: Instant
)