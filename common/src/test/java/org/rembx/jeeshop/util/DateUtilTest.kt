package org.rembx.jeeshop.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class DateUtilTest {
    @Test
    fun testDateToLocalDateTime() {
        val date = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z").parse("2001.07.04 12:08:56 UTC")
        val localDateTime = DateUtil.dateToLocalDateTime(date)
        assertThat(LocalDateTime.of(2001, Month.JULY, 4, 12, 8, 56).atZone(ZoneId.of("Z")))
            .isEqualTo(localDateTime)
    }
}
