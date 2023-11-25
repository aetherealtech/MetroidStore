package aetherealtech.metroidstore.customerclient.utilities

import kotlinx.datetime.Instant
import java.text.DateFormat
import java.util.Date

val Instant.displayString: String
    get() {

        return DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT
        ).format(Date(toEpochMilliseconds()))
    }