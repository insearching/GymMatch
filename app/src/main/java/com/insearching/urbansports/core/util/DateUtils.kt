package com.insearching.urbansports.core.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateUtils {

    fun String.formatTime(): String? {
        return try {
            val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val outputFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

            val zonedDateTime = ZonedDateTime.parse(this, inputFormatter)
            outputFormatter.format(zonedDateTime)
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}