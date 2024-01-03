package aetherealtech.metroidstore.customerclient.backendclient

import android.content.res.Resources
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderActivity
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import aetherealtech.metroidstore.customerclient.model.Rating
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import java.net.HttpURLConnection

class AuthenticatedBackendClient internal constructor(
    private val host: HttpUrl,
    private val token: String,
    private val onLogout: () -> Unit
) {
    suspend fun getProducts(query: String?): ImmutableList<ProductSummary> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("products")
                .addQueryParameter(name = "query", value = query)
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendProducts = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.ProductSummary>>(
            body.string()
        )

        return backendProducts
            .map { backendProduct ->
                ProductSummary(
                    id = ProductID(backendProduct.id),
                    image = ImageSourceBackend(client, host, backendProduct.image),
                    name = backendProduct.name,
                    type = backendProduct.type,
                    game = backendProduct.game,
                    price = Price(backendProduct.priceCents),
                    ratingCount = backendProduct.ratingCount,
                    rating = backendProduct.rating
                )
            }
            .toImmutableList()
    }

    suspend fun getProductDetails(id: ProductID): ProductDetails {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("products")
                .addPathSegment("${id.value}")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw Resources.NotFoundException("No product with ID ${id.value}")

        val backendProduct = Json.decodeFromString<aetherealtech.metroidstore.backendmodel.ProductDetails>(
            body.string()
        )

        return ProductDetails(
            id = ProductID(backendProduct.id),
            name = backendProduct.name,
            type = backendProduct.type,
            game = backendProduct.game,
            images = backendProduct.images
                .map { image -> ImageSourceBackend(client, host, image) }
                .toImmutableList(),
            ratings = backendProduct.ratings
                .map { rawRating -> Rating.parse(rawRating) }
                .toImmutableList(),
            price = Price(backendProduct.priceCents)
        )
    }

    private fun handleCartResponse(response: Response): ImmutableList<CartItem> {
        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendCartItems = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.CartItem>>(
            body.string()
        )

        return backendCartItems
            .map { backendCartItem ->
                CartItem(
                    productID = ProductID(backendCartItem.productID),
                    image = ImageSourceBackend(client, host, backendCartItem.image),
                    name = backendCartItem.name,
                    pricePerUnit = Price(backendCartItem.priceCents),
                    quantity = backendCartItem.quantity
                )
            }
            .toImmutableList()
    }

    suspend fun getCart(): ImmutableList<CartItem> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("cart")
        }

        val response = client.newCall(request).await()

        return handleCartResponse(response)
    }

    suspend fun addToCart(productID: ProductID): ImmutableList<CartItem> {
        val request = buildRequest(
            modifyRequest = { builder -> builder.post(byteArrayOf().toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("cart")
                .addPathSegment("${productID.value}")
        }

        val response = client.newCall(request).await()

        return handleCartResponse(response)
    }

    suspend fun removeFromCart(productID: ProductID): ImmutableList<CartItem> {
        val request = buildRequest(
            modifyRequest = { builder -> builder.delete() }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("cart")
                .addPathSegment("${productID.value}")
        }

        val response = client.newCall(request).await()

        return handleCartResponse(response)
    }

    suspend fun decrementQuantity(productID: ProductID): ImmutableList<CartItem> {
        val request = buildRequest(
            modifyRequest = { builder -> builder.post(byteArrayOf().toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("cart")
                .addPathSegment("${productID.value}")
                .addPathSegment("decrement")
        }

        val response = client.newCall(request).await()

        return handleCartResponse(response)
    }

    suspend fun incrementQuantity(productID: ProductID): ImmutableList<CartItem> {
        val request = buildRequest(
            modifyRequest = { builder -> builder.post(byteArrayOf().toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("cart")
                .addPathSegment("${productID.value}")
                .addPathSegment("increment")
        }

        val response = client.newCall(request).await()

        return handleCartResponse(response)
    }
    
    suspend fun getAddresses(): ImmutableList<UserAddressSummary> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("addresses")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendAddresses = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.UserAddressSummary>>(
            body.string()
        )

        return backendAddresses
            .map { backendAddress ->
                UserAddressSummary(
                    addressID = Address.ID(backendAddress.addressID),
                    name = backendAddress.name,
                    isPrimary = backendAddress.isPrimary
                )
            }
            .toImmutableList()
    }

    suspend fun getShippingMethods(): ImmutableList<ShippingMethod> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("shippingMethods")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendMethods = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.ShippingMethod>>(
            body.string()
        )

        return backendMethods
            .map { backendMethod ->
                ShippingMethod(
                    name = backendMethod.name,
                    cost = Price(backendMethod.costCents)
                )
            }
            .toImmutableList()
    }

    suspend fun getPaymentMethods(): ImmutableList<PaymentMethodSummary> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("paymentMethods")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendMethods = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.PaymentMethodSummary>>(
            body.string()
        )

        return backendMethods
            .map { backendMethod ->
                PaymentMethodSummary(
                    id = PaymentMethodID(backendMethod.id),
                    name = backendMethod.name,
                    isPrimary = backendMethod.isPrimary
                )
            }
            .toImmutableList()
    }

    suspend fun getOrders(): ImmutableList<OrderSummary> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("orders")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendOrders = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.OrderSummary>>(
            body.string()
        )

        return backendOrders
            .map { backendOrder ->
                OrderSummary(
                    id = OrderID(backendOrder.id),
                    date = Instant.parse(backendOrder.date),
                    items = backendOrder.items,
                    total = Price(backendOrder.totalCents),
                    latestStatus = OrderStatus.parse(backendOrder.latestStatus)
                )
            }
            .toImmutableList()
    }

    suspend fun getOrder(id: OrderID): OrderDetails {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("orders")
                .addPathSegment("${id.value}")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendOrder = Json.decodeFromString<aetherealtech.metroidstore.backendmodel.OrderDetails>(
            body.string()
        )

        return OrderDetails(
            date = Instant.parse(backendOrder.date),
            address = backendOrder.address,
            shippingMethod = backendOrder.shippingMethod,
            paymentMethod = backendOrder.paymentMethod,
            total = Price(backendOrder.totalCents),
            latestStatus = OrderStatus.parse(backendOrder.latestStatus),
            items = backendOrder.items
                .map { backendItem ->
                    OrderDetails.Item(
                        productID = ProductID(backendItem.productID),
                        name = backendItem.name,
                        image = ImageSourceBackend(client, host, backendItem.image),
                        quantity = backendItem.quantity,
                        price = Price(backendItem.priceCents)
                    )
                }
                .toImmutableList()
        )
    }

    suspend fun getOrderActivity(id: OrderID): ImmutableList<OrderActivity> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("orders")
                .addPathSegment("${id.value}")
                .addPathSegment("activity")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendActivities = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.OrderActivity>>(
            body.string()
        )

        return backendActivities
            .map { backendActivity ->
                OrderActivity(
                    status = OrderStatus.parse(backendActivity.status),
                    date = Instant.parse(backendActivity.date)
                )
            }
            .toImmutableList()
    }

    suspend fun placeOrder(order: NewOrder): OrderID {
        val backendOrder = aetherealtech.metroidstore.backendmodel.NewOrder(
            addressID = order.addressID.value,
            shippingMethodName = order.shippingMethod.name,
            paymentMethodID = order.paymentMethodID.value
        )

        val request = buildRequest(
            modifyRequest = { builder -> builder.post(Json.encodeToString(backendOrder).toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("orders")
        }

        val response = client.newCall(request).await()

        if(!response.isSuccessful)
            throw IllegalStateException(response.message)

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        return OrderID(body.string().toInt())
    }

    suspend fun getAddressDetails(): ImmutableList<UserAddressDetails> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("addresses")
                .addPathSegment("details")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendAddresses = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.UserAddressDetails>>(
            body.string()
        )

        return backendAddresses
            .map { backendAddress -> backendAddress.clientModel }
            .toImmutableList()
    }

    suspend fun getPaymentMethodDetails(): ImmutableList<PaymentMethodDetails> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("paymentMethods")
                .addPathSegment("details")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendPaymentMethods = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.PaymentMethodDetails>>(
            body.string()
        )

        return backendPaymentMethods
            .map { paymentMethod -> paymentMethod.clientModel }
            .toImmutableList()
    }

    suspend fun createAddress(address: EditAddress): ImmutableList<UserAddressDetails> {
        val backendNewAddress = aetherealtech.metroidstore.backendmodel.EditAddress(
            name = address.name,
            street1 = address.street1.value,
            street2 = address.street2?.value,
            locality = address.locality.value,
            province = address.province.value,
            country = address.country.value,
            planet = address.planet.value,
            postalCode = address.postalCode?.value,
            isPrimary = address.isPrimary
        )

        val request = buildRequest(
            modifyRequest = { builder -> builder.post(Json.encodeToString(backendNewAddress).toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("addresses")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendAddresses = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.UserAddressDetails>>(
            body.string()
        )

        return backendAddresses
            .map { backendAddress -> backendAddress.clientModel }
            .toImmutableList()
    }

    suspend fun updateAddress(address: EditAddress, id: Address.ID): ImmutableList<UserAddressDetails> {
        val backendNewAddress = aetherealtech.metroidstore.backendmodel.EditAddress(
            name = address.name,
            street1 = address.street1.value,
            street2 = address.street2?.value,
            locality = address.locality.value,
            province = address.province.value,
            country = address.country.value,
            planet = address.planet.value,
            postalCode = address.postalCode?.value,
            isPrimary = address.isPrimary
        )

        val request = buildRequest(
            modifyRequest = { builder -> builder.patch(Json.encodeToString(backendNewAddress).toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("addresses")
                .addPathSegment("${id.value}")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendAddresses = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.UserAddressDetails>>(
            body.string()
        )

        return backendAddresses
            .map { backendAddress -> backendAddress.clientModel }
            .toImmutableList()
    }

    suspend fun deleteAddress(id: Address.ID): ImmutableList<UserAddressDetails> {
        val request = buildRequest(
            modifyRequest = { builder -> builder.delete() }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("addresses")
                .addPathSegment("${id.value}")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendAddresses = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.UserAddressDetails>>(
            body.string()
        )

        return backendAddresses
            .map { backendAddress -> backendAddress.clientModel }
            .toImmutableList()
    }

    suspend fun createPaymentMethod(paymentMethod: EditPaymentMethod): ImmutableList<PaymentMethodDetails> {
        val backendNewPaymentMethod = aetherealtech.metroidstore.backendmodel.EditPaymentMethod(
            name = paymentMethod.name,
            number = paymentMethod.number.value,
            isPrimary = paymentMethod.isPrimary
        )

        val request = buildRequest(
            modifyRequest = { builder -> builder.post(Json.encodeToString(backendNewPaymentMethod).toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("paymentMethods")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendPaymentMethods = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.PaymentMethodDetails>>(
            body.string()
        )

        return backendPaymentMethods
            .map { backendPaymentMethod -> backendPaymentMethod.clientModel }
            .toImmutableList()
    }

    suspend fun updatePaymentMethod(paymentMethod: EditPaymentMethod, id: PaymentMethodID): ImmutableList<PaymentMethodDetails> {
        val backendNewPaymentMethod = aetherealtech.metroidstore.backendmodel.EditPaymentMethod(
            name = paymentMethod.name,
            number = paymentMethod.number.value,
            isPrimary = paymentMethod.isPrimary
        )

        val request = buildRequest(
            modifyRequest = { builder -> builder.patch(Json.encodeToString(backendNewPaymentMethod).toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("paymentMethods")
                .addPathSegment("${id.value}")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendPaymentMethods = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.PaymentMethodDetails>>(
            body.string()
        )

        return backendPaymentMethods
            .map { backendPaymentMethod -> backendPaymentMethod.clientModel }
            .toImmutableList()
    }

    suspend fun deletePaymentMethod(id: PaymentMethodID): ImmutableList<PaymentMethodDetails> {
        val request = buildRequest(
            modifyRequest = { builder -> builder.delete() }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("paymentMethods")
                .addPathSegment("${id.value}")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendPaymentMethods = Json.decodeFromString<List<aetherealtech.metroidstore.backendmodel.PaymentMethodDetails>>(
            body.string()
        )

        return backendPaymentMethods
            .map { backendPaymentMethod -> backendPaymentMethod.clientModel }
            .toImmutableList()
    }

    suspend fun logout() {
        val request = buildRequest(
            modifyRequest = { builder -> builder.post(byteArrayOf().toRequestBody()) }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("logout")
        }

        val response = client.newCall(request).await()

        if(!response.isSuccessful)
            throw IllegalArgumentException("Logout Failed")

        onLogout()
    }

    private val client = OkHttpClient()

    private fun buildRequest(
        modifyRequest: (Request.Builder) -> Unit = { },
        modifyURL: (HttpUrl.Builder) -> Unit
    ): Request {
        val urlBuilder = host.newBuilder()
        modifyURL(urlBuilder)

        val requestBuilder = Request.Builder()
            .url(urlBuilder.build())
            .header(name = "Authorization", value = token)
        modifyRequest(requestBuilder)

        return requestBuilder
            .build()
    }
}

val aetherealtech.metroidstore.backendmodel.UserAddressDetails.clientModel: UserAddressDetails
    get() = UserAddressDetails(
        name = name,
        address = Address(
            id = Address.ID(addressID),
            street1 = Address.Street1(street1),
            street2 = street2?.let { street2 -> Address.Street2(street2) },
            locality = Address.Locality(locality),
            province = Address.Province(province),
            country = Address.Country(country),
            planet = Address.Planet(planet),
            postalCode = postalCode?.let { postalCode -> Address.PostalCode(postalCode) }
        ),
        isPrimary = isPrimary
    )

val aetherealtech.metroidstore.backendmodel.PaymentMethodDetails.clientModel: PaymentMethodDetails
    get() = PaymentMethodDetails(
        id = PaymentMethodID(id),
        name = name,
        number = PaymentMethodDetails.Number(number),
        isPrimary = isPrimary
    )