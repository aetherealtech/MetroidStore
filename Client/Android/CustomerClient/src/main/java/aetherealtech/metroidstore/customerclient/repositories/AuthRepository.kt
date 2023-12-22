package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.AuthDataSource
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val dataSource: AuthDataSource
) {
    private val _busy = MutableStateFlow(false)

    val busy = _busy
        .asStateFlow()

    suspend fun login(
        username: String,
        password: String
    ): DataSource {
        _busy.value = true

        try {
            val dataSource = dataSource.login(username, password)
            return dataSource
        } finally {
            _busy.value = false
        }
    }
}