package aetherealtech.metroidstore.customerclient.ui.loginflow

import aetherealtech.metroidstore.customerclient.datasources.api.DataSource
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.ui.login.LoginViewModel
import aetherealtech.metroidstore.customerclient.ui.signup.SignUpViewModel
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import androidx.lifecycle.ViewModel

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