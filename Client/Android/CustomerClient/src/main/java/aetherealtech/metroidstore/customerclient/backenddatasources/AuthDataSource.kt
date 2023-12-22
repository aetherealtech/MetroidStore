package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.AuthDataSource
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import okhttp3.HttpUrl

class AuthDataSourceBackend(
    host: HttpUrl
): AuthDataSource {
    private val client = BackendClient(host)

    override suspend fun login(username: String, password: String): DataSource {
        val authenticatedClient = client.login(username, password)
        return DataSourceBackend(client = authenticatedClient)
    }
}