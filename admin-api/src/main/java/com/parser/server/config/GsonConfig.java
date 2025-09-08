package com.parser.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import com.parser.core.config.gson.*;
import com.parser.core.util.DateFormatUtil;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;

@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GsonConfig {

    GsonAdapterFactory gsonAdapterFactory;

    @Bean
    public Gson gsonBean() {
        return new GsonBuilder()
                .setDateFormat(DateFormatUtil.DATE_TIME_FORMAT)
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(byte[].class, new ByteArrayDeserializer())
                .registerTypeAdapterFactory(new HibernateProxyAdapterFactory())
                .registerTypeAdapter(Json.class, (JsonSerializer<Json>) (src, typeOfSrc, context) ->
                        new JsonParser().parse(src.value()))
                .registerTypeAdapterFactory(gsonAdapterFactory)
                .setExclusionStrategies(GsonExclusionStrategy.getInstance())
                .create();
    }
}
