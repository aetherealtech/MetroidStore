package com.example.metroidstore.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownList(
    viewModel: DropDownViewModel<T>,
    optionDisplay: (T) -> String,
    additionalOption: @Composable () -> Unit = { }
) {
    val expanded = remember { mutableStateOf(false) }

    val currentExpanded by expanded
    val currentOptions by viewModel.options.collectAsState()
    val currentSelection by viewModel.selection.collectAsState()

    ExposedDropdownMenuBox(
        expanded = currentExpanded,
        onExpandedChange = { expanded.value = !currentExpanded }
    ) {
        TextField(
            readOnly = true,
            value = currentSelection?.let { option -> optionDisplay(option) } ?: "",
            modifier = Modifier.menuAnchor(),
            onValueChange = { },
            label = { Text(viewModel.title) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = currentExpanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = currentExpanded,
            onDismissRequest = { expanded.value = false }
        ) {
            currentOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionDisplay(option)) },
                    onClick = {
                        expanded.value = false
                        viewModel.selection.value = option
                    },
                    trailingIcon = if (currentSelection == option) ({
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }) else null
                )
            }

            viewModel.additionalOption?.let { select ->
                DropdownMenuItem(
                    text = { additionalOption() },
                    onClick = {
                        expanded.value = false
                        select()
                    }
                )
            }
        }
    }
}

class DropDownViewModel<T>(
    val title: String,
    val options: StateFlow<ImmutableList<T>>,
    selectedOption: T? = null,
    val additionalOption: (() -> Unit)? = null
) {
    val selection = MutableStateFlow(selectedOption)
}