package com.example.metroidstore.backendclient

import android.content.res.Resources
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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
    suspend fun getProducts(): ImmutableList<ProductSummary> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("products")
        }

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendProducts = Json.decodeFromString<List<com.example.metroidstore.backendmodel.ProductSummary>>(
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
                    ratings = backendProduct.ratings.map { rawRating -> Rating.values().first { rating -> rating.value == rawRating } }.toImmutableList(),
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

        val backendProduct = Json.decodeFromString<com.example.metroidstore.backendmodel.ProductDetails>(
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
            ratings = backendProduct.ratings.map { rawRating -> Rating.values().first { rating -> rating.value == rawRating } }.toImmutableList(),
            price = Price(backendProduct.priceCents)
        )
    }

    private fun handleCartResponse(response: Response): List<CartItem> {
        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val backendCartItems = Json.decodeFromString<List<com.example.metroidstore.backendmodel.CartItem>>(
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

    suspend fun getCart(): List<CartItem> {
        val request = buildRequest { urlBuilder ->
            urlBuilder
                .addPathSegment("cart")
        }

        val response = client.newCall(request).await()

        return handleCartResponse(response)
    }

    suspend fun addToCart(productID: ProductID): List<CartItem> {
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

    suspend fun removeFromCart(productID: ProductID): List<CartItem> {
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

    suspend fun decrementQuantity(productID: ProductID): List<CartItem> {
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

    suspend fun incrementQuantity(productID: ProductID): List<CartItem> {
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
}