package aetherealtech.metroidstore.customerclient.ui.root

import aetherealtech.metroidstore.customerclient.ui.loginflow.LoginFlowView
import aetherealtech.metroidstore.customerclient.ui.main.MainView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

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