package com.example.metroidstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.metroidstore.embeddedbackend.EmbeddedDatabase
import com.example.metroidstore.embeddedbackend.ProductDataSourceEmbedded
import com.example.metroidstore.productlist.ProductList
import com.example.metroidstore.productlist.ProductListViewModel
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetroidStoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProductList(
                        viewModel = ProductListViewModel(
                            productRepository = ProductRepository(
                                dataSource = ProductDataSourceEmbedded(
                                    database = EmbeddedDatabase.load(this)
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}

