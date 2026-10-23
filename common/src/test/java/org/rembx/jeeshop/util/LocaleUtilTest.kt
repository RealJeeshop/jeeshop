package org.rembx.jeeshop.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocaleUtilTest {
    @Test
    fun getLocaleCode_shouldReturnLocaleCode() {
        assertThat(LocaleUtil.getLocaleCode("en_GB")).isEqualTo("en_GB")
    }

    @Test
    fun getLocaleCode_shouldFallbackForInvalidLocale() {
        assertThat(LocaleUtil.getLocaleCode("qsdpjsd")).isEqualTo(LocaleUtil.FALLBACK.toString())
    }

    @Test
    fun getLocaleCode_shouldFallbackWhenNullLocaleProvided() {
        assertThat(LocaleUtil.getLocaleCode(null)).isEqualTo(LocaleUtil.FALLBACK.toString())
    }
}
