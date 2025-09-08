package com.parser.server.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumAdapterFactory implements TypeAdapterFactory {
    private static final Logger log = LoggerFactory.getLogger(EnumAdapterFactory.class);

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType.isEnum()) {
            return new EnumTypeAdapter<>();
        }
        return null;
    }

    public static class EnumTypeAdapter<T> extends TypeAdapter<T> {
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null || !value.getClass().isEnum()) {
                out.nullValue();
                return;
            }

            try {
                out.beginObject();
                out.name("value");
                out.value(value.toString());
                Arrays.stream(Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors())
                        .filter(pd -> pd.getReadMethod() != null && !"class".equals(pd.getName()) && !"declaringClass".equals(pd.getName()))
                        .forEach(pd -> {
                            try {
                                out.name(pd.getName());
                                out.value(String.valueOf(pd.getReadMethod().invoke(value)));
                            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                                log.warn("Failed to write enum property '{}'", pd.getName(), e);
                            }
                        });
                out.endObject();
            } catch (IntrospectionException e) {
                log.warn("Failed to introspect enum bean info", e);
            }
        }

        public T read(JsonReader in) throws IOException {
            return null;
        }
    }
}
