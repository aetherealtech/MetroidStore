package com.example.metroidstore.fakedatasources

import android.content.res.Resources.NotFoundException
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.ImageSourceData
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class ProductDataSourceFake(
    private val products: List<DataSourceFake.Product>
): ProductDataSource {
    override suspend fun getProducts(): ImmutableList<ProductSummary> {
        return products
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
            ratings = ratings,
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