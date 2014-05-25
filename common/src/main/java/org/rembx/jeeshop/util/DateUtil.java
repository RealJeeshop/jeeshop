package org.rembx.jeeshop.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by remi on 25/05/14.
 */
public class DateUtil {

    public static LocalDateTime dateToLocalDateTime(Date date){
        Instant endDateInstant = Instant.ofEpochMilli(date.getTime());
       return  LocalDateTime.ofInstant(endDateInstant, ZoneOffset.UTC);
    }
}
