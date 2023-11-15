package com.example.metroidstore.repositories

import com.example.metroidstore.datasources.DataSource
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import kotlinx.collections.immutable.ImmutableList

class ProductRepository(
    private val dataSource: DataSource
) {
    private val productsProductDataSource = dataSource.products

    val cart = CartRepository(dataSource.cart)
    suspend fun getProducts(): ImmutableList<ProductSummary> {
        return productsProductDataSource.getProducts()
    }

    suspend fun getProductDetails(id: ProductID): ProductDetails {
        return productsProductDataSource.getProductDetails(id)
    }
}