package aetherealtech.metroidstore.customerclient.ui.signup

import aetherealtech.metroidstore.customerclient.datasources.fake.AuthDataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.uitoolkit.PrimaryCallToAction
import aetherealtech.metroidstore.customerclient.widgets.BusyView
import aetherealtech.metroidstore.customerclient.widgets.LabeledValidatedPasswordField
import aetherealtech.metroidstore.customerclient.widgets.LabeledValidatedTextField
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