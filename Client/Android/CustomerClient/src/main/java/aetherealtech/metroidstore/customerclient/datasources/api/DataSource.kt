package aetherealtech.metroidstore.customerclient.datasources.api

interface DataSource {
    val products: ProductDataSource
    val cart: CartDataSource
    val user: UserDataSource
    val orders: OrderDataSource

    suspend fun logout()
}