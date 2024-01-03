package aetherealtech.metroidstore.customerclient.ui.root

import aetherealtech.metroidstore.customerclient.datasources.backend.AuthDataSourceBackend
import aetherealtech.metroidstore.customerclient.datasources.api.DataSource
import aetherealtech.metroidstore.customerclient.ui.loginflow.LoginFlowView
import aetherealtech.metroidstore.customerclient.ui.loginflow.LoginFlowViewModel
import aetherealtech.metroidstore.customerclient.ui.main.MainView
import aetherealtech.metroidstore.customerclient.ui.main.MainViewModel
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.embeddedbackend.EmbeddedServer
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl

@Composable
fun RootView(
    viewModel: RootViewModel
) {
    val content by viewModel.content.collectAsState()

    when(val currentContent = content) {
        is RootViewModel.Login -> LoginFlowView(viewModel = currentContent.viewModel)
        is RootViewModel.Root -> MainView(viewModel = currentContent.viewModel)
    }
}

class RootViewModel private constructor(
    context: Context
): ViewModel() {
    private val embeddedServer: EmbeddedServer
    private val authDataSource: AuthDataSourceBackend

    private val _content: MutableStateFlow<Content>

    sealed class Content

    class Login(val viewModel: LoginFlowViewModel): Content()
    class Root(val viewModel: MainViewModel): Content()

    init {
        val port: UShort = 6785u

        embeddedServer = EmbeddedServer(
            port = port,
            context = context
        )

        val backendHost = HttpUrl.Builder()
            .scheme("http")
            .host("localhost")
            .port(port.toInt())
            .build()

        authDataSource = AuthDataSourceBackend(
            host = backendHost,
            context
        )

        val savedLogin = authDataSource.savedLogin

        val initialContent: Content

        if(savedLogin != null)
            initialContent = Root(root(savedLogin))

        else
            initialContent = Login(login())

        _content = MutableStateFlow(initialContent)
    }

    val content = _content
        .asStateFlow()

    private fun login() = LoginFlowViewModel(
        repository = AuthRepository(
            dataSource = authDataSource
        ),
        onLoggedIn = { dataSource -> _content.value = Root(root(dataSource)) }
    )

    private fun root(
        dataSource: DataSource
    ) = MainViewModel(
        dataSource = dataSource,
        logout = { logout(dataSource) }
    )

    companion object {
        fun Factory(
            context: Context
        ) = object : ViewModelFactory<RootViewModel>() {
            override fun create() = RootViewModel(
                context
            )
        }
    }

    private fun logout(
        dataSource: DataSource
    ) {
        viewModelScope.launch {
            dataSource.logout()

            _content.value = Login(login())
        }
    }
}