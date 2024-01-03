package aetherealtech.metroidstore.customerclient.datasources.fake

import android.content.res.Resources.NotFoundException
import aetherealtech.metroidstore.customerclient.datasources.api.ProductDataSource
import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import aetherealtech.metroidstore.customerclient.model.rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ProductDataSourceFake(
    private val products: List<DataSourceFake.Product>
): ProductDataSource {
    override suspend fun getProducts(query: String?): ImmutableList<ProductSummary> {
        return products
            .filter { product -> query?.let { query -> product.name.contains(query) } ?: true }
            .map { product -> product.summary }
            .toImmutableList()
    }

    override suspend fun getProductDetails(id: ProductID): ProductDetails {
        val product = products
            .find { product -> product.id == id }

        if(product == null)
            throw NotFoundException("No product with ID ${id.value}")

        return product.details
    }
}

val DataSourceFake.Product.summary: ProductSummary
    get() {
        return ProductSummary(
            id = id,
            image = images[0],
            name = name,
            type = type,
            game = game,
            ratingCount = ratings.size,
            rating = ratings.rating,
            price = price
        )
    }

val DataSourceFake.Product.details: ProductDetails
    get() {
        return ProductDetails(
            id = id,
            images = images,
            name = name,
            type = type,
            game = game,
            ratings = ratings,
            price = price
        )
    }