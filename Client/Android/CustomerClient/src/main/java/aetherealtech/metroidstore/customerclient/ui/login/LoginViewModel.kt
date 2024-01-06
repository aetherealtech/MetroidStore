package aetherealtech.metroidstore.customerclient.ui.login

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.datasources.api.DataSource
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.metroidstore.customerclient.widgets.FormValue
import aetherealtech.metroidstore.customerclient.widgets.requiredNonEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    repository: AuthRepository,
    val openSignup: () -> Unit,
    onLoggedIn: (DataSource) -> Unit
): ViewModel() {
    private val _error = MutableSharedFlow<String>()

    val busy = repository.busy
    val error = _error.asSharedFlow()

    val username = FormValue.requiredNonEmpty("")
    val password = FormValue.requiredNonEmpty("")

    val login: StateFlow<(() -> Unit)?> = StateFlows.combine(
        this.username.value,
        this.password.value
    )
        .mapState { values ->
            val first = values.first
            val second = values.second

            if(
                first == null ||
                second == null
            )
                return@mapState null

            return@mapState {
                viewModelScope.launch {
                    try {
                        val dataSource = repository.login(
                            username = first,
                            password = second
                        )

                        onLoggedIn(dataSource)
                    } catch(error: Exception) {
                        when(error) {
                            is BackendClient.InvalidLoginException -> _error.emit("Invalid username or password")
                            else -> _error.emit("Error communicating with server.  Please try again")
                        }
                    }
                }
            }
        }
}