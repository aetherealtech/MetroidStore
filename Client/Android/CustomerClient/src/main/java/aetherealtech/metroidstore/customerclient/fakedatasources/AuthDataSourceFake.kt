package aetherealtech.metroidstore.customerclient.fakedatasources

import aetherealtech.metroidstore.customerclient.datasources.AuthDataSource
import aetherealtech.metroidstore.customerclient.datasources.DataSource

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