package com.insearching.urbansports.core.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    fun String.formatTime(): String {
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        val zonedDateTime = ZonedDateTime.parse(this, inputFormatter)
        return outputFormatter.format(zonedDateTime)
    }
}