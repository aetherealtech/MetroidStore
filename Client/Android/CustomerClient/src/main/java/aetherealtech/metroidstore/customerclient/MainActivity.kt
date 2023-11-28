package aetherealtech.metroidstore.customerclient

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import aetherealtech.metroidstore.customerclient.backenddatasources.DataSourceBackend
import aetherealtech.metroidstore.embeddedbackend.EmbeddedServer
import aetherealtech.metroidstore.customerclient.root.RootView
import aetherealtech.metroidstore.customerclient.root.RootViewModel
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.customerclient.utilities.viewModel
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import okhttp3.HttpUrl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels { MainViewModel.Factory(this) }

        setContent {
            MetroidStoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    RootView(
                        viewModel = viewModel(
                            factory = viewModel.root
                        )
                    )
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

    val root = object : ViewModelFactory<RootViewModel>() {
        override fun create() = RootViewModel(
            dataSource = DataSourceBackend(
                host = backendHost
            )
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