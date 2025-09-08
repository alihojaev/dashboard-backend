package com.parser.core.util.filter;

import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Value
public class DateTimeFilter {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    ZonedDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    ZonedDateTime until;


    private static final DateTimeFilter EMPTY = new DateTimeFilter(null, null);
}
