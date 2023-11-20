package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BottomModal(
    data: Flow<T>,
    content: @Composable (T, () -> Unit) -> Unit
) {
    val currentData = remember { mutableStateOf<T?>(null) }

    LaunchedEffect(data) {
        data.collect { value ->
            currentData.value = value
        }
    }

    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { false }
    )

    val coroutineScope = rememberCoroutineScope()

    currentData.value?.let { activeData ->
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = sheetState
        ) {
            content(
                activeData
            ) {
                coroutineScope.launch {
                    sheetState.hide()
                    currentData.value = null
                }
            }
        }
    }
}