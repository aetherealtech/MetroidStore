package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.DataSource
import aetherealtech.metroidstore.customerclient.model.ProductID

class ProductRepository(
    private val dataSource: DataSource
) {
    private val productsProductDataSource = dataSource.products

    val cart = CartRepository(dataSource.cart)
    suspend fun getProducts(query: String?) = productsProductDataSource.getProducts(query)
    suspend fun getProductDetails(id: ProductID) = productsProductDataSource.getProductDetails(id)
}