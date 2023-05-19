package com.franc.standard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    /**
     * Now-LocalDateTime => 문자열 (yyyyMMddHHmmss)
     * @return
     */
    public static String nowDateToString() {
        return localDateTimeToString(LocalDateTime.now());
    }

    /**
     * LocalDateTime => 문자열 (yyyyMMddHHmmss)
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if(localDateTime == null)
            localDateTime = LocalDateTime.now();

        return localDateTime.format((DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

}
