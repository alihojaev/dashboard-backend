package com.parser.core.util.rest;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Slf4j
public abstract class BaseRestDto implements Serializable {

    protected static final Gson G = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
            .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
            .create();

    public String json() {
        return G.toJson(this);
    }

    public String jsonForLog() {
        return G.toJson(clone());
    }

    @Override
    protected Object clone() {
        return this;
    }
}
