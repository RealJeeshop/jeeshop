package org.rembx.jeeshop.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by remi on 25/05/14.
 */
public class DateUtil {

    public static ZonedDateTime dateToLocalDateTime(Date date){
        Instant endDateInstant = Instant.ofEpochMilli(date.getTime());
       return  ZonedDateTime.ofInstant(endDateInstant, ZoneOffset.UTC);
    }
}
