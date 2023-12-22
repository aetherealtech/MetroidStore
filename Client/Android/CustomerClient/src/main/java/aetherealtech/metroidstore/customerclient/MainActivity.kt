package aetherealtech.metroidstore.customerclient

import aetherealtech.metroidstore.customerclient.backenddatasources.AuthDataSourceBackend
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import aetherealtech.metroidstore.customerclient.login.LoginView
import aetherealtech.metroidstore.customerclient.login.LoginViewModel
import aetherealtech.metroidstore.customerclient.repositories.AuthRepository
import aetherealtech.metroidstore.customerclient.root.RootView
import aetherealtech.metroidstore.customerclient.root.RootViewModel
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.customerclient.utilities.viewModel
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import okhttp3.HttpUrl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels { MainViewModel.Factory(this) }

        setThemedContent {
            LoginView(
                viewModel = viewModel(
                    factory = viewModel.login(
                        onLoggedIn = { dataSource -> onLoggedIn(viewModel, dataSource )}
                    )
                )
            )
        }
    }

    private fun onLoggedIn(
        viewModel: MainViewModel,
        dataSource: DataSource
    ) {
        setThemedContent {
            RootView(
                viewModel = viewModel(
                    factory = viewModel.root(
                        dataSource = dataSource
                    )
                )
            )
        }
    }

    private fun setThemedContent(
        content: @Composable () -> Unit
    ) {
        setContent {
            MetroidStoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    content()
                }
            }
        }
    }
}

class MainViewModel private constructor(
    context: Context
): ViewModel() {
    private val embeddedServer: EmbeddedServer
    private val backendHost: HttpUrl

    init {
        val port: UShort = 6785u

        embeddedServer = EmbeddedServer(
            port = port,
            context = context
        )

        backendHost = HttpUrl.Builder()
            .scheme("http")
            .host("localhost")
            .port(port.toInt())
            .build()
    }

    fun login(
        onLoggedIn: (DataSource) -> Unit
    ) = object : ViewModelFactory<LoginViewModel>() {
        override fun create() = LoginViewModel(
            repository = AuthRepository(
                dataSource = AuthDataSourceBackend(
                    host = backendHost
                )
            ),
            onLoggedIn = onLoggedIn
        )
    }

    fun root(
        dataSource: DataSource
    ) = object : ViewModelFactory<RootViewModel>() {
        override fun create() = RootViewModel(
            dataSource = dataSource
        )
    }

    companion object {
        fun Factory(
            context: Context
        ) = object : ViewModelFactory<MainViewModel>() {
            override fun create() = MainViewModel(
                context
            )
        }
    }
}