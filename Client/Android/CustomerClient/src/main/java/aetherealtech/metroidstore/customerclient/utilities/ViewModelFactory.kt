package aetherealtech.metroidstore.customerclient.utilities

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

abstract class ViewModelFactory<VM: ViewModel>: ViewModelProvider.Factory {
    abstract fun create(): VM

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        return create() as T
    }
}

@Composable
inline fun <reified VM: ViewModel> viewModel(
    factory: ViewModelFactory<VM>
): VM {
    return androidx.lifecycle.viewmodel.compose.viewModel(
        factory = factory
    )
}