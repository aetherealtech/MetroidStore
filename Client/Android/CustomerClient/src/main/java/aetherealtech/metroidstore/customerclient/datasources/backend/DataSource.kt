package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.AuthenticatedBackendClient
import aetherealtech.metroidstore.customerclient.datasources.api.DataSource

class DataSourceBackend(
    private val client: AuthenticatedBackendClient
): DataSource {
    override val products = ProductDataSourceBackend(client)
    override val cart = CartDataSourceBackend(client)
    override val user = UserDataSourceBackend(client)
    override val orders = OrderDataSourceBackend(client)

    override suspend fun logout() = client.logout()
}