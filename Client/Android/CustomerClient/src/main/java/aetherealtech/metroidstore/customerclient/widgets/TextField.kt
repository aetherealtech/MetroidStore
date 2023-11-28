package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.material3.TextField

@Composable
fun TextField(
    value: MutableStateFlow<String>
) {
    val currentValue by value.collectAsState()

    TextField(
        value = currentValue,
        onValueChange = { newValue -> value.value = newValue },
        singleLine = true
    )
}