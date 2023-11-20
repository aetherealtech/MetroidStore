package aetherealtech.metroidstore.customerclient.model

import kotlinx.datetime.Instant

data class OrderSummary(
    val id: OrderID,
    val date: Instant,
    val items: Int,
    val total: Price,
    val latestStatus: OrderStatus
)