package org.rembx.jeeshop.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocaleUtilTest {

    @Test
    public void getLocaleCode_shouldReturnLocaleCode() throws Exception {
        assertThat(LocaleUtil.getLocaleCode("en_GB")).isEqualTo("en_GB");
    }

    @Test
    public void getLocaleCode_shouldFallbackForInvalidLocale() throws Exception {
        assertThat(LocaleUtil.getLocaleCode("qsdpjsd")).isEqualTo(LocaleUtil.FALLBACK.toString());
    }

    @Test
    public void getLocaleCode_shouldFallbackWhenNullLocaleProvided() throws Exception {
        assertThat(LocaleUtil.getLocaleCode(null)).isEqualTo(LocaleUtil.FALLBACK.toString());
    }
}