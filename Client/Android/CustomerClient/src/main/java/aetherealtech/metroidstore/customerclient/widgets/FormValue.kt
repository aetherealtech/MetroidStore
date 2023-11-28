package aetherealtech.metroidstore.customerclient.widgets

import aetherealtech.metroidstore.customerclient.utilities.mapState
import kotlinx.coroutines.flow.MutableStateFlow

class FormValue<T>(
    value: T,
    format: (T) -> String,
    parse: (String) -> Result<T>
) {
    val displayValue = MutableStateFlow(format(value))

    private val _value = displayValue
        .mapState(parse)

    val value = _value
        .mapState { value ->
            value.getOrNull()
        }

    val error = _value
        .mapState { value ->
            value.exceptionOrNull()
        }

    companion object
}

fun FormValue.Companion.requiredNonEmpty(
    value: String
): FormValue<String> = FormValue(
    value = value,
    format = { input -> input },
    parse = { input ->
        if (input.isEmpty()) Result.failure(IllegalStateException("Must be non-empty")) else Result.success(
            input
        )
    }
)

fun FormValue.Companion.optionalNonEmpty(
    value: String?
): FormValue<String?> = FormValue(
    value = value,
    format = { input -> input ?: "" },
    parse = { input -> Result.success(if (input.isEmpty()) null else input) }
)