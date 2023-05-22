package com.franc.standard.config;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class H2FunctionConfig {

    public static String dateFormat(LocalDateTime date, String format) throws Exception {
        if(date == null || !StringUtils.hasText(format)) return null;

        return date.format(DateTimeFormatter.ofPattern(convertMySqlFormat(format)));
    }




    public static String convertMySqlFormat(String s) throws Exception {
        StringBuilder result = new StringBuilder();

        if(s.indexOf("%Y") > -1)
            result.append("yyyy");
        if(s.indexOf("%m") > -1)
            result.append("MM");
        if(s.indexOf("%d") > -1)
            result.append("dd");
        if(s.indexOf("%H") > -1)
            result.append("HH");
        if(s.indexOf("%i") > -1)
            result.append("mm");
        if(s.indexOf("%s") > -1)
            result.append("ss");

        return result.toString();
    }
}
