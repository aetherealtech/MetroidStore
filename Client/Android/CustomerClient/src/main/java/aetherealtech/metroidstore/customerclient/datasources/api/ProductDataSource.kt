package aetherealtech.metroidstore.customerclient.datasources.api

import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import kotlinx.collections.immutable.ImmutableList

interface ProductDataSource {
    suspend fun getProducts(query: String?): ImmutableList<ProductSummary>
    suspend fun getProductDetails(id: ProductID): ProductDetails
}