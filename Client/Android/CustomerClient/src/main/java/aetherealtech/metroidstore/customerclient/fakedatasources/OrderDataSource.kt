package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.OrderDataSource
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import aetherealtech.metroidstore.customerclient.model.Price
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class OrderDataSourceFake(
    private val products: ImmutableList<DataSourceFake.Product>
): OrderDataSource {
    override suspend fun getOrders(): ImmutableList<OrderSummary> {
        return persistentListOf(
            OrderSummary(
                id = OrderID(1),
                date = Clock.System.now(),
                items = 5,
                latestStatus = OrderStatus.PREPARING,
                total = Price(16500)
            ),
            OrderSummary(
                id = OrderID(2),
                date = Clock.System.now() - 2.days,
                items = 3,
                latestStatus = OrderStatus.SHIPPED,
                total = Price(11500)
            ),
            OrderSummary(
                id = OrderID(3),
                date = Clock.System.now() - 8.days,
                items = 4,
                latestStatus = OrderStatus.DELIVERED,
                total = Price(22400)
            )
        )
    }

    override suspend fun getOrder(id: OrderID): OrderDetails {
        return OrderDetails(
            date = Clock.System.now(),
            address = "That Place",
            shippingMethod = "Fast",
            paymentMethod = "Theft",
            total = Price(14999),
            latestStatus = OrderStatus.SHIPPED,
            items = persistentListOf(
                products[0].orderItem(quantity = 2),
                products[1].orderItem(quantity = 1),
                products[2].orderItem(quantity = 3)
            )
        )
    }
}

fun DataSourceFake.Product.orderItem(
    quantity: Int
): OrderDetails.Item = OrderDetails.Item(
        productID = id,
        name = name,
        image = images[0],
        quantity = quantity,
        price = price * quantity
    )