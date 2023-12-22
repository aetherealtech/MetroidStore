package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LabeledControl(
    name: String,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .width(128.dp),
            text = "$name: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
fun LabeledSwitch(
    name: String,
    value: MutableStateFlow<Boolean>
) {
    LabeledControl(
        name = name
    ) {
        Switch(value = value)
    }
}

@Composable
fun LabeledTextField(
    name: String,
    value: MutableStateFlow<String>
) {
    LabeledControl(
        name = name
    ) {
        TextField(value = value)
    }
}

@Composable
fun <T> LabeledValidatedTextField(
    name: String,
    value: FormValue<T>
) {
    LabeledControl(
        name = name
    ) {
        ValidatedTextField(value = value)
    }
}

@Composable
fun LabeledValidatedPasswordField(
    name: String,
    value: FormValue<String>
) {
    LabeledControl(
        name = name
    ) {
        ValidatedPasswordField(value = value)
    }
}