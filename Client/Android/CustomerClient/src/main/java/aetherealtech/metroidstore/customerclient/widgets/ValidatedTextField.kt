package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.aetherealtech.metroidstore.customerclient.R

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

@Composable
fun ValidatedPasswordField(
    value: FormValue<String>
) {
    val displayedValue by value.displayValue.collectAsState()
    val error by value.error.collectAsState()
    var visible by remember { mutableStateOf(false) }

    TextField(
        value = displayedValue,
        onValueChange = { newValue -> value.displayValue.value = newValue },
        singleLine = true,
        isError = error != null,
        placeholder = { Text("Password") },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (visible)
                R.drawable.baseline_visibility_off_24 else
                R.drawable.baseline_visibility_24

            val description = if (visible) "Hide password" else "Show password"

            IconButton(
                onClick = { visible = !visible }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = image),
                    contentDescription = description
                )
            }
        }
    )
}