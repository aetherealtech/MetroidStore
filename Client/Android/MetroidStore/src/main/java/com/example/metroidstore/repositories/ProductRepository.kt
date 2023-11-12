package com.example.metroidstore.repositories

import com.example.metroidstore.model.Product
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.datasources.ProductDataSourceFake
import kotlinx.collections.immutable.ImmutableList

class ProductRepository(
    private val dataSource: ProductDataSource = ProductDataSourceFake()
) {
    suspend fun getProducts(): ImmutableList<Product> {
        return dataSource.getProducts()
    }
}