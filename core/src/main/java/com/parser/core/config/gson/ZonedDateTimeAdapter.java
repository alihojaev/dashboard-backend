package com.parser.core.config.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends TypeAdapter<ZonedDateTime> {

    @Override
    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
        if (value != null) {
            out.value(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        } else {
            out.value(String.valueOf(value));
        }
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException {
        return ZonedDateTime.parse(in.nextString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
