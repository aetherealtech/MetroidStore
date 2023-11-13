package com.example.metroidstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.metroidstore.backenddatasources.ProductDataSourceBackend
import com.example.metroidstore.embeddedbackend.EmbeddedServer
import com.example.metroidstore.productlist.ProductList
import com.example.metroidstore.productlist.ProductListViewModel
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import okhttp3.HttpUrl

class MainActivity : ComponentActivity() {
    private lateinit var embeddedServer: EmbeddedServer
    private lateinit var backendHost: HttpUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val port: UShort = 6785u

        embeddedServer = EmbeddedServer(
            port = port,
            context = this
        )

        backendHost = HttpUrl.Builder()
            .scheme("http")
            .host("localhost")
            .port(port.toInt())
            .build()

        setContent {
            MetroidStoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProductList(
                        viewModel = ProductListViewModel(
                            productRepository = ProductRepository(
                                dataSource = ProductDataSourceBackend(
                                    host = backendHost
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}

