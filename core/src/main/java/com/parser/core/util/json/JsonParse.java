package com.parser.core.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonParse {

    public static <T> T parse(String result, String field, Class<T> tClass) {
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(result);

        return new Gson().fromJson(object.get(field), tClass);
    }

}
