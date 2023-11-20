package aetherealtech.metroidstore.customerclient.uitoolkit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import aetherealtech.metroidstore.customerclient.widgets.BottomModal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <ContentVM, VM: ConfirmationModalViewModel<ContentVM>> ConfirmationModal(
    viewModel: Flow<VM>,
    content: @Composable (ContentVM, () -> Unit) -> Unit
) {
    BottomModal(
        data = viewModel
    ) { currentViewModel, onClose ->
        val currentContentViewModel by currentViewModel.content.collectAsState()

        currentContentViewModel?.let { contentViewModel ->
            content(
                contentViewModel,
                onClose
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                )
            }
        }
    }
}

open class ConfirmationModalViewModel<Content: ViewModel>(
    val content: StateFlow<Content?>
): ViewModel()