package com.example.metroidstore.backenddatasources

import com.example.metroidstore.backendclient.BackendClient
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import kotlinx.collections.immutable.ImmutableList

class ProductDataSourceBackend(
    private val client: BackendClient
): ProductDataSource {
    override suspend fun getProducts(): ImmutableList<ProductSummary> = client.getProducts()

    override suspend fun getProductDetails(id: ProductID): ProductDetails = client.getProductDetails(id)
}