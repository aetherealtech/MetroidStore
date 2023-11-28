package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.material3.Switch

@Composable
fun Switch(
    value: MutableStateFlow<Boolean>
) {
    val currentValue by value.collectAsState()

    Switch(
        checked = currentValue,
        onCheckedChange = { newValue -> value.value = newValue }
    )
}