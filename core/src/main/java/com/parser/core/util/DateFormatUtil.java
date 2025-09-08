package com.parser.core.util;

import com.parser.core.exceptions.BadRequestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_TRACK = "yyyyMMdd";
    public static final String DATE_FORMAT_EXCEL = "dd.MM.yyyy";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }

    public static SimpleDateFormat trackDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT_TRACK);
    }

    public static SimpleDateFormat dateTimeFormat() {
        return new SimpleDateFormat(DATE_TIME_FORMAT);
    }

    public static SimpleDateFormat dateTimeFormatExcel() {
        return new SimpleDateFormat(DATE_FORMAT_EXCEL);
    }

    public static Date parseDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new BadRequestException("wrong date format");
        }
    }

    public static Date parseDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new BadRequestException("wrong date format");
        }
    }
}
