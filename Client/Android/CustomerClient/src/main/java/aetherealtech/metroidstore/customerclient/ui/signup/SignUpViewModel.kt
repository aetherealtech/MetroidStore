package aetherealtech.metroidstore.customerclient.ui.signup

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.androiduitoolkit.FormValue
import aetherealtech.androiduitoolkit.requiredNonEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    repository: AuthRepository,
    private val onCompleted: () -> Unit
): ViewModel() {
    private val _error = MutableSharedFlow<String>()

    val busy = repository.busy
    val error = _error.asSharedFlow()

    val username = FormValue.requiredNonEmpty("")
    val password = FormValue.requiredNonEmpty("")

    val signUp: StateFlow<(() -> Unit)?> = StateFlows.combine(
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
                        repository.signUp(
                            username = first,
                            password = second
                        )

                        onCompleted()
                    } catch(error: Exception) {
                        when(error) {
                            is BackendClient.InvalidSignUpException -> _error.emit("Invalid username or password")
                            else -> _error.emit("Error communicating with server.  Please try again")
                        }
                    }
                }
            }
        }

    fun cancel() {
        onCompleted()
    }
}