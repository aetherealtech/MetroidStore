package com.example.metroidstore.backenddatasources

import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.Product
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

    override suspend fun getProducts(): ImmutableList<Product> {
        val request = Request.Builder()
            .url(host.newBuilder().addPathSegment("products").build())
            .build()

        val response = client.newCall(request).await()

        val body = response.body

        if(body == null)
            return persistentListOf()

        val backendProducts = Json.decodeFromString<List<com.example.metroidstore.embeddedbackend.Product>>(
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

                return@map Product(
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
}