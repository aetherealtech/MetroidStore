package aetherealtech.metroidstore.customerclient.ui.loginflow

import aetherealtech.metroidstore.customerclient.datasources.api.DataSource
import aetherealtech.metroidstore.customerclient.datasources.fake.AuthDataSourceFake
import aetherealtech.metroidstore.customerclient.ui.login.LoginView
import aetherealtech.metroidstore.customerclient.ui.login.LoginViewModel
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.ui.signup.SignUpView
import aetherealtech.metroidstore.customerclient.ui.signup.SignUpViewModel
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.customerclient.utilities.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginFlowView(
    viewModel: LoginFlowViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = "login",
    ) {
        composable(
            "login"
        ) {
            val loginViewModel = viewModel(
                factory = viewModel.login(
                    openSignup = { navController.navigate("signUp") }
                )
            )

            LoginView(
                viewModel = loginViewModel
            )
        }

        composable(
            "signUp"
        ) {
            val signupViewModel = viewModel(
                factory = viewModel.signUp(
                    onCompleted = { navController.popBackStack() }
                )
            )

            SignUpView(
                viewModel = signupViewModel
            )
        }
    }
}

class LoginFlowViewModel(
    private val repository: AuthRepository,
    private val onLoggedIn: (DataSource) -> Unit
): ViewModel() {
    fun login(
        openSignup: () -> Unit
    ) = object : ViewModelFactory<LoginViewModel>() {
        override fun create() = LoginViewModel(
            repository = repository,
            openSignup = openSignup,
            onLoggedIn = onLoggedIn
        )
    }

    fun signUp(
        onCompleted: () -> Unit
    ) = object : ViewModelFactory<SignUpViewModel>() {
        override fun create() = SignUpViewModel(
            repository = repository,
            onCompleted = onCompleted
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFlowPreview() {
    MetroidStoreTheme {
        LoginFlowView(
            viewModel = LoginFlowViewModel(
                repository = AuthRepository(
                    dataSource = AuthDataSourceFake(),
                ),
                onLoggedIn = { }
            )
        )
    }
}