package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.AuthenticatedBackendClient
import aetherealtech.metroidstore.customerclient.datasources.ProductDataSource
import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import kotlinx.collections.immutable.ImmutableList

class ProductDataSourceBackend(
    private val client: AuthenticatedBackendClient
): ProductDataSource {
    override suspend fun getProducts(query: String?): ImmutableList<ProductSummary> = client.getProducts(query)

    override suspend fun getProductDetails(id: ProductID): ProductDetails = client.getProductDetails(id)
}