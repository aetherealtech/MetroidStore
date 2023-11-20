package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class UserDataSourceFake: UserDataSource {
    override suspend fun getAddresses(): ImmutableList<UserAddressSummary> {
        return persistentListOf(
            UserAddressSummary(Address.ID(0), "Home", isPrimary = true),
            UserAddressSummary(Address.ID(0), "Office", isPrimary = false),
            UserAddressSummary(Address.ID(0), "Secret Lair", isPrimary = false),
        )
    }

    override suspend fun getShippingMethods(): ImmutableList<ShippingMethod> {
        return persistentListOf(
            ShippingMethod("Slow", Price(1000)),
            ShippingMethod("Fast", Price(5000)),
        )
    }

    override suspend fun getPaymentMethods(): ImmutableList<PaymentMethodSummary> {
        return persistentListOf(
            PaymentMethodSummary(PaymentMethodSummary.ID(0), "Credit", isPrimary = false),
            PaymentMethodSummary(PaymentMethodSummary.ID(1), "Theft", isPrimary = true),
        )
    }

    override suspend fun placeOrder(order: NewOrder): OrderID {
        return OrderID(0)
    }
}