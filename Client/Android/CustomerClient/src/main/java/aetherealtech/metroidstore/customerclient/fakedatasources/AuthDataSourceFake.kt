package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.AuthDataSource
import aetherealtech.metroidstore.customerclient.datasources.DataSource

class AuthDataSourceFake: AuthDataSource {
    override suspend fun login(
        username: String,
        password: String
    ): DataSource {
        return DataSourceFake()
    }
}