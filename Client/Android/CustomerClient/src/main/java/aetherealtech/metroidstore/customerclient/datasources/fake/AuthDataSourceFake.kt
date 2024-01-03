package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.api.AuthDataSource
import aetherealtech.metroidstore.customerclient.datasources.api.DataSource

class AuthDataSourceFake: AuthDataSource {
    override val savedLogin: DataSource = DataSourceFake()

    override suspend fun login(
        username: String,
        password: String
    ): DataSource {
        return DataSourceFake()
    }

    override suspend fun signUp(
        username: String,
        password: String
    ) {

    }
}