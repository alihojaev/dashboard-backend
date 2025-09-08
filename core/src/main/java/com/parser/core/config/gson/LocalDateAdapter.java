package com.parser.core.config.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter implements JsonSerializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return new JsonPrimitive(formatter.format(localDateTime));
    }
}
