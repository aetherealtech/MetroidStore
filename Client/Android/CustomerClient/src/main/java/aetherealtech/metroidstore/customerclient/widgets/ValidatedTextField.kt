package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.TextField

@Composable
fun <T> ValidatedTextField(
    value: FormValue<T>
) {
    val displayedValue by value.displayValue.collectAsState()
    val error by value.error.collectAsState()

    TextField(
        value = displayedValue,
        onValueChange = { newValue -> value.displayValue.value = newValue },
        singleLine = true,
        isError = error != null
    )
}