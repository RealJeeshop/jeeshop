package org.rembx.jeeshop.util

import org.apache.commons.lang3.LocaleUtils
import org.slf4j.LoggerFactory
import java.util.Locale

object LocaleUtil {
    private val logger = LoggerFactory.getLogger(LocaleUtil::class.java)

    @JvmField
    val FALLBACK: Locale = Locale.ENGLISH

    @JvmStatic
    fun getLocaleCode(localeStr: String?): String {
        val locale = try {
            if (localeStr != null) LocaleUtils.toLocale(localeStr) else FALLBACK
        } catch (e: IllegalArgumentException) {
            logger.warn("cannot get locale from {}. Returning fallback locale: {}", localeStr, FALLBACK)
            FALLBACK
        }
        return locale.toString()
    }
}
