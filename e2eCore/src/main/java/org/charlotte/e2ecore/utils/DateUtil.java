package org.charlotte.e2ecore.utils;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class DateUtil {

    public static Date convertFrom(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime convertFrom(Date date){
        if(Objects.isNull(date)){
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }
}
