package com.parser.core.config.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter implements JsonSerializer<LocalTime> {
    @Override
    public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return new JsonPrimitive(formatter.format(localTime));
    }
}
