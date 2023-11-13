package com.example.metroidstore.backenddatasources

import android.content.res.Resources.NotFoundException
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

class ProductDataSourceBackend(
    private val host: HttpUrl
): ProductDataSource {
    private val client = OkHttpClient()

    override suspend fun getProducts(): ImmutableList<ProductSummary> {
        val request = Request.Builder()
            .url(host.newBuilder()
                .addPathSegment("products")
                .build()
            )
            .build()

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            return persistentListOf()

        val backendProducts = Json.decodeFromString<List<com.example.metroidstore.embeddedbackend.ProductSummary>>(
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

    override suspend fun getProductDetails(id: ProductID): ProductDetails {
        val request = Request.Builder()
            .url(host.newBuilder()
                .addPathSegment("products")
                .addPathSegment("${id.value}")
                .build()
            )
            .build()

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            throw NotFoundException("No product with ID ${id.value}")

        val backendProduct = Json.decodeFromString<com.example.metroidstore.embeddedbackend.ProductDetails>(
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
            name = backendProduct.name,
            type = backendProduct.type,
            game = backendProduct.game,
            images = images,
            ratings = backendProduct.ratings.map { rawRating -> Rating.values().first { rating -> rating.value == rawRating } }.toImmutableList(),
            price = Price(backendProduct.priceCents)
        )
    }
}