package com.example.metroidstore.repositories

import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import kotlinx.collections.immutable.ImmutableList

class ProductRepository(
    private val dataSource: ProductDataSource
) {
    suspend fun getProducts(): ImmutableList<ProductSummary> {
        return dataSource.getProducts()
    }

    suspend fun getProductDetails(id: ProductID): ProductDetails {
        return dataSource.getProductDetails(id)
    }
}