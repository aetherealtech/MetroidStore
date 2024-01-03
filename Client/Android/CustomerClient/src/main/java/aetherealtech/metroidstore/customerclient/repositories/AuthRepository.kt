package aetherealtech.metroidstore.customerclient.repositories

import aetherealtech.metroidstore.customerclient.datasources.api.AuthDataSource
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
    ) = perform { dataSource.login(username, password) }

    suspend fun signUp(
        username: String,
        password: String
    ) = perform { dataSource.signUp(username, password) }

    private suspend fun <T> perform(
        action: suspend () -> T
    ): T {
        _busy.value = true

        try {
            return action()
        } finally {
            _busy.value = false
        }
    }
}