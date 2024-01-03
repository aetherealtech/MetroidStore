package aetherealtech.metroidstore.customerclient.signup

import aetherealtech.metroidstore.customerclient.backendclient.BackendClient
import aetherealtech.metroidstore.customerclient.fakedatasources.AuthDataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import aetherealtech.metroidstore.customerclient.utilities.StateFlows
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.BusyView
import aetherealtech.metroidstore.customerclient.widgets.FormValue
import aetherealtech.metroidstore.customerclient.widgets.LabeledValidatedPasswordField
import aetherealtech.metroidstore.customerclient.widgets.LabeledValidatedTextField
import aetherealtech.metroidstore.customerclient.widgets.requiredNonEmpty
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Composable
fun SignUpView(
    viewModel: SignUpViewModel
) {
    val create by viewModel.signUp.collectAsState()

    BusyView(
        busy = viewModel.busy
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledValidatedTextField(
                    name = "Username",
                    value = viewModel.username
                )
                LabeledValidatedPasswordField(
                    name = "Password",
                    value = viewModel.password
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryCallToAction(
                    modifier = Modifier
                        .width(128.dp),
                    onClick = create,
                    text = "Sign Up"
                )

                TextButton(
                    onClick = { viewModel.cancel() }
                ) {
                    Text("Cancel")
                }
            }
        }

        var currentError by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(viewModel.error) {
            viewModel.error.collect { value ->
                currentError = value
            }
        }

        currentError?.let { error ->
            AlertDialog(
                title = { Text(text = "Error") },
                text = { Text(text = error) },
                onDismissRequest = { currentError = null },
                confirmButton = {
                    TextButton(
                        onClick = { currentError = null }
                    ) {
                        Text("Retry")
                    }
                }
            )
        }
    }
}

class SignUpViewModel(
    repository: AuthRepository,
    private val onCompleted: () -> Unit
): ViewModel() {
    private val _error = MutableSharedFlow<String>()

    val busy = repository.busy
    val error = _error.asSharedFlow()

    val username = FormValue.requiredNonEmpty("")
    val password = FormValue.requiredNonEmpty("")

    val signUp: StateFlow<(() -> Unit)?> = StateFlows
        .combine(
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

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    MetroidStoreTheme {
        SignUpView(
            viewModel = SignUpViewModel(
                repository = AuthRepository(
                    dataSource = AuthDataSourceFake(),
                ),
                onCompleted = { }
            )
        )
    }
}