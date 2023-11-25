package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun <T> CenterModal(
    data: Flow<T>,
    content: @Composable (T, () -> Unit) -> Unit
) {
    val currentData = remember { mutableStateOf<T?>(null) }

    LaunchedEffect(data) {
        data.collect { value ->
            currentData.value = value
        }
    }

    currentData.value?.let { activeData ->
        Dialog(
            onDismissRequest = { currentData.value = null },
            properties = DialogProperties(
                dismissOnClickOutside = true
            )
        ) {
            content(
                activeData
            ) {
                currentData.value = null
            }
        }
    }
}