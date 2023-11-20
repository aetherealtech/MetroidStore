package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import okhttp3.HttpUrl

class DataSourceBackend(
    host: HttpUrl
): DataSource {
    private val client = BackendClient(host)

    override val products = ProductDataSourceBackend(client)
    override val cart = CartDataSourceBackend(client)
    override val user = UserDataSourceBackend(client)
    override val orders = OrderDataSourceBackend(client)
}