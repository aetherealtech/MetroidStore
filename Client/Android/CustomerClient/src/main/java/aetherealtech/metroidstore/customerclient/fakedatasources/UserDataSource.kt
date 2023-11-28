package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.UserDataSource
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class UserDataSourceFake: UserDataSource {
    private val _addresses = persistentListOf(
        UserAddressDetails(
            name = "Home",
            address = Address(
                id = Address.ID(0),
                street1 = Address.Street1("123 Fake St."),
                street2 = null,
                locality = Address.Locality("Fakerton"),
                province = Address.Province("CA"),
                country = Address.Country("USA"),
                planet = Address.Planet("Earth"),
                postalCode = Address.PostalCode("90210")
            ),
            isPrimary = true
        ),
        UserAddressDetails(
            name = "Office",
            address = Address(
                id = Address.ID(1),
                street1 = Address.Street1("5634 Main St."),
                street2 = Address.Street2("#43"),
                locality = Address.Locality("Fakerton"),
                province = Address.Province("CA"),
                country = Address.Country("USA"),
                planet = Address.Planet("Earth"),
                postalCode = Address.PostalCode("90210")
            ),
            isPrimary = false
        ),
        UserAddressDetails(
            name = "Secret Lair",
            address = Address(
                id = Address.ID(2),
                street1 = Address.Street1("41114 Definitely Not Here Ave."),
                street2 = null,
                locality = Address.Locality("Shhh"),
                province = Address.Province("CA"),
                country = Address.Country("USA"),
                planet = Address.Planet("Earth"),
                postalCode = Address.PostalCode("90210")
            ),
            isPrimary = false
        )
    )
    override suspend fun getAddresses(): ImmutableList<UserAddressSummary> {
        return _addresses
            .map { address -> address.summary }
            .toImmutableList()
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

    override suspend fun getAddressDetails(): ImmutableList<UserAddressDetails> {
        return _addresses
    }

    override suspend fun placeOrder(order: NewOrder): OrderID {
        return OrderID(0)
    }

    override suspend fun createAddress(address: EditAddress): ImmutableList<UserAddressDetails> {
        return _addresses
    }

    override suspend fun updateAddress(address: EditAddress, id: Address.ID): ImmutableList<UserAddressDetails> {
        return _addresses
    }
}

val UserAddressDetails.summary: UserAddressSummary
    get() = UserAddressSummary(
        addressID = address.id,
        name = name,
        isPrimary = isPrimary
    )