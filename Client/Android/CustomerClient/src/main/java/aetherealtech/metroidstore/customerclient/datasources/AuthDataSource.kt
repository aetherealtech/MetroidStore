package aetherealtech.metroidstore.customerclient.datasources

interface AuthDataSource {
    val savedLogin: DataSource?

    suspend fun login(
        username: String,
        password: String
    ): DataSource

    suspend fun signUp(
        username: String,
        password: String
    )
}