package org.rembx.jeeshop.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

object DateUtil {
    @JvmStatic
    fun dateToLocalDateTime(date: Date): ZonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneOffset.UTC)
}
