package aetherealtech.metroidstore.customerclient

import aetherealtech.metroidstore.customerclient.backenddatasources.AuthDataSourceBackend
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import aetherealtech.metroidstore.customerclient.loginflow.LoginFlowView
import aetherealtech.metroidstore.customerclient.loginflow.LoginFlowViewModel
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.root.RootView
import aetherealtech.metroidstore.customerclient.root.RootViewModel
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.embeddedbackend.EmbeddedServer
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels { MainViewModel.Factory(this) }

        setContent {
            MetroidStoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainView(
    viewModel: MainViewModel
) {
    val content by viewModel.content.collectAsState()

    when(val currentContent = content) {
        is MainViewModel.Login -> LoginFlowView(viewModel = currentContent.viewModel)
        is MainViewModel.Root -> RootView(viewModel = currentContent.viewModel)
    }
}

class MainViewModel private constructor(
    context: Context
): ViewModel() {
    private val embeddedServer: EmbeddedServer
    private val authDataSource: AuthDataSourceBackend

    private val _content: MutableStateFlow<Content>

    sealed class Content

    class Login(val viewModel: LoginFlowViewModel): Content()
    class Root(val viewModel: RootViewModel): Content()

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
        onLoggedIn = { dataSource ->  _content.value = Root(root(dataSource)) }
    )

    private fun root(
        dataSource: DataSource
    ) = RootViewModel(
        dataSource = dataSource,
        logout = { logout(dataSource) }
    )

    companion object {
        fun Factory(
            context: Context
        ) = object : ViewModelFactory<MainViewModel>() {
            override fun create() = MainViewModel(
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