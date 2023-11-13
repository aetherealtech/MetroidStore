package com.example.metroidstore

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metroidstore.backenddatasources.DataSourceBackend
import com.example.metroidstore.backenddatasources.ProductDataSourceBackend
import com.example.metroidstore.embeddedbackend.EmbeddedServer
import com.example.metroidstore.productlist.ProductListViewModel
import com.example.metroidstore.root.RootView
import com.example.metroidstore.root.RootViewModel
import com.example.metroidstore.ui.theme.MetroidStoreTheme
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

    val root: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return RootViewModel(
                dataSource = DataSourceBackend(
                    host = backendHost
                )
            ) as T
        }
    }

    companion object {
        fun Factory(
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T: ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return MainViewModel(
                    context
                ) as T
            }
        }
    }
}