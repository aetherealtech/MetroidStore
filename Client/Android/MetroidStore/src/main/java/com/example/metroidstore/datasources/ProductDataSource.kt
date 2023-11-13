package com.example.metroidstore.datasources

import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import kotlinx.collections.immutable.ImmutableList

interface ProductDataSource {
    suspend fun getProducts(): ImmutableList<ProductSummary>
    suspend fun getProductDetails(id: ProductID): ProductDetails
}