package com.example.metroidstore.repositories

import com.example.metroidstore.model.Product
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.fakedatasources.ProductDataSourceFake
import kotlinx.collections.immutable.ImmutableList

class ProductRepository(
    private val dataSource: ProductDataSource
) {
    suspend fun getProducts(): ImmutableList<Product> {
        return dataSource.getProducts()
    }

    suspend fun getProductDetails(id: Product.ID): Product {
        return dataSource.getProductDetails(id)
    }
}