package aetherealtech.metroidstore.customerclient.datasources

interface DataSource {
    val products: ProductDataSource
    val cart: CartDataSource
    val user: UserDataSource
    val orders: OrderDataSource
}