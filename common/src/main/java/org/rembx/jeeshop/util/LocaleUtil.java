package org.rembx.jeeshop.util;

import org.apache.commons.lang.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class LocaleUtil {

    private static Logger logger = LoggerFactory.getLogger(LocaleUtil.class);

    public static final Locale FALLBACK = Locale.ENGLISH;

    public static String getLocaleCode(String localeStr) {
        Locale locale = FALLBACK;
        try {
            locale = (localeStr != null)? LocaleUtils.toLocale(localeStr):FALLBACK;
        } catch (IllegalArgumentException e) {
            logger.warn("cannot get locale from {}. Returning fallback locale: "+FALLBACK,localeStr);
        }
        return locale.toString();
    }
}
