package aetherealtech.metroidstore.customerclient.backenddatasources

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.AuthDataSource
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import android.content.Context
import okhttp3.HttpUrl

class AuthDataSourceBackend(
    host: HttpUrl,
    context: Context
): AuthDataSource {
    private val client = BackendClient(
        host,
        context
    )

    override val savedLogin: DataSource? = client.savedLogin?.let { client -> DataSourceBackend(client = client) }

    override suspend fun login(username: String, password: String): DataSource {
        val authenticatedClient = client.login(username, password)
        return DataSourceBackend(client = authenticatedClient)
    }

    override suspend fun signUp(username: String, password: String) {
        client.signUp(username, password)
    }
}