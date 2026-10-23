package org.rembx.jeeshop.configuration

import jakarta.enterprise.inject.Produces
import java.util.Calendar
import java.util.GregorianCalendar

class TestProducer {
    @Produces
    private fun calendar(): Calendar = calendar

    companion object {
        @JvmField
        val calendar: Calendar = GregorianCalendar(2013, 0, 9, 18, 1, 0)
    }
}
