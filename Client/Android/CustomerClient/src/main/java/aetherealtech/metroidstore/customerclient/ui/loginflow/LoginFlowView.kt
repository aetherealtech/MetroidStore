package aetherealtech.metroidstore.customerclient.ui.loginflow

import aetherealtech.metroidstore.customerclient.datasources.fake.AuthDataSourceFake
import aetherealtech.metroidstore.customerclient.ui.login.LoginView
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.ui.signup.SignUpView
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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