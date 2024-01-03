package aetherealtech.metroidstore.customerclient

import aetherealtech.metroidstore.customerclient.root.RootView
import aetherealtech.metroidstore.customerclient.root.RootViewModel
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RootViewModel by viewModels { RootViewModel.Factory(this) }

        setContent {
            MetroidStoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootView(viewModel = viewModel)
                }
            }
        }
    }
}