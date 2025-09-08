package com.parser.core.util.filter;

import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Value
public class DateFilter {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDateTime until;
}
