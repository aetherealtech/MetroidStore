package aetherealtech.metroidstore.customerclient.model

enum class OrderStatus(val value: String) {
    PREPARING("Preparing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    DELAYED("Delayed"),
    CANCELLED("Cancelled");

    companion object {
        fun parse(value: String): OrderStatus {
            return values().firstOrNull { status -> status.value == value }
                ?: throw IllegalArgumentException("$value is not a valid OrderStatus")
        }
    }
}