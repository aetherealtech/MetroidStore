package aetherealtech.metroidstore.customerclient.datasources

interface AuthDataSource {
    suspend fun login(
        username: String,
        password: String
    ): DataSource
}