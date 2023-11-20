package aetherealtech.metroidstore.customerclient.backendclient

import android.content.res.Resources
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import aetherealtech.metroidstore.customerclient.model.Rating
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
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

class BackendClient(
    private val host: HttpUrl
) {
    suspend fun getProducts(): ImmutableList<ProductSummary> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("products")
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
                val imageRequest = Request.Builder()
                    .url(host.newBuilder()
                        .addEncodedPathSegments(backendProduct.image)
                        .build()
                    )
                    .build()

                return@map ProductSummary(
                    id = ProductID(backendProduct.id),
                    image = ImageSourceBackend(client, imageRequest),
                    name = backendProduct.name,
                    type = backendProduct.type,
                    game = backendProduct.game,
                    ratings = backendProduct.ratings.map { rawRating -> Rating.parse(rawRating) }.toImmutableList(),
                    price = Price(backendProduct.priceCents)
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

        val images = backendProduct.images
            .map { image ->
                Request.Builder()
                    .url(
                        host.newBuilder()
                            .addEncodedPathSegments(image)
                            .build()
                    )
                    .build()
            }
            .map { imageRequest ->
                ImageSourceBackend(client, imageRequest)
            }
            .toImmutableList()

        return ProductDetails(
            id = ProductID(backendProduct.id),
            name = backendProduct.name,
            type = backendProduct.type,
            game = backendProduct.game,
            images = images,
            ratings = backendProduct.ratings.map { rawRating -> Rating.parse(rawRating) }.toImmutableList(),
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
                val imageRequest = Request.Builder()
                    .url(host.newBuilder()
                        .addEncodedPathSegments(backendCartItem.image)
                        .build()
                    )
                    .build()

                return@map CartItem(
                    productID = ProductID(backendCartItem.productID),
                    image = ImageSourceBackend(client, imageRequest),
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
                    id = PaymentMethodSummary.ID(backendMethod.id),
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

    private val client = OkHttpClient()
    private val username = "mother_brain"

    private fun buildRequest(
        modifyRequest: (Request.Builder) -> Unit = { },
        modifyURL: (HttpUrl.Builder) -> Unit
    ): Request {
        val urlBuilder = host.newBuilder()
        modifyURL(urlBuilder)

        val requestBuilder = Request.Builder()
            .url(urlBuilder.build())
            .header(name = "Authorization", value = username)
        modifyRequest(requestBuilder)

        return requestBuilder
            .build()
    }
}