package com.parser.core.config.gson;

import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.parser.core.exceptions.BadRequestException;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.ConsumerE;
import com.parser.core.util.functional.FunctionE;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE)
public class GsonAdapterFactory implements TypeAdapterFactory, ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType.isEnum()) {
            //noinspection unchecked,rawtypes
            return new EnumTypeAdapter(rawType);
        }

        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @FieldDefaults(level = PRIVATE, makeFinal = true)
    static class DiscussionModelTypeAdapter extends TypeAdapter<Object> {
        Class<?> type;
        Gson gson;

        @Value
        static class FieldProcessContainer {
            Field field;
            FunctionE<Object, Object, IllegalAccessException> getter;
            BiConsumerE<Object, Object, IllegalAccessException> setter;
        }

        Map<String, FieldProcessContainer> fieldsMap;

        public DiscussionModelTypeAdapter(
                Class<?> type,
                Gson gson
        ) {
            List<Class<?>> types = new ArrayList<>();
            types.add(type);

            Class<?> subType = type;
            while ((subType = subType.getSuperclass()) != Object.class) types.add(subType);

            fieldsMap = types.stream().flatMap(t -> Stream.of(t.getDeclaredFields()))
                    .filter(Predicate.not(
                                    field ->
                                            GsonExclusionStrategy.getInstance().shouldSkipField(new FieldAttributes(field)) ||
                                                    GsonExclusionStrategy.getInstance().shouldSkipClass(field.getType())
                            )
                    )
                    .collect(Collectors.toMap(
                            Field::getName,
                            field -> new FieldProcessContainer(
                                    field,
                                    obj -> {
                                        field.setAccessible(true);
                                        try {
                                            return field.get(obj);
                                        } finally {
                                            field.setAccessible(false);
                                        }
                                    },
                                    (obj, value) -> {
                                        field.setAccessible(true);
                                        try {
                                            field.set(obj, value);
                                        } finally {
                                            field.setAccessible(false);
                                        }
                                    }
                            )
                    ));
            this.type = type;
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();

            for (var entry : fieldsMap.entrySet()) {
                try {
                    out.name(entry.getKey()).jsonValue(gson.toJson(entry.getValue().getter.apply(value)));
                } catch (IllegalAccessException e) {
                    throw new IOException(e);
                }
            }

            out.endObject();
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_OBJECT:
                    try {
                        var discussable = type.getConstructor().newInstance();
                        in.beginObject();
                        while (in.hasNext()) {
                            var fieldProcessContainer = fieldsMap.get(in.nextName());
                            if (fieldProcessContainer != null) {
                                var o = gson.fromJson(in, fieldProcessContainer.field.getGenericType());
                                fieldProcessContainer.setter.accept(discussable, o);
                            } else {
                                in.skipValue();
                            }
                        }
                        in.endObject();
                        return discussable;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new IOException(e);
                    }

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }
    }

    public static class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {

        final Class<T> type;

        public EnumTypeAdapter(Class<?> type) {
            //noinspection unchecked
            this.type = (Class<T>) type;
        }

        private static <T> boolean writeWithType(Class<T> type, Object o, ConsumerE<T, IOException> writer) throws IOException {
            if (type == o.getClass()) {
                //noinspection unchecked
                writer.accept((T) o);
                return true;
            } else {
                return false;
            }
        }

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
                                var param = pd.getReadMethod().invoke(value);
                                if (param != null) {
                                    out.name(pd.getName());
                                    if (param.getClass().isEnum()) {
                                        //noinspection unchecked
                                        write(out, (T) param);
                                    } else if (!(
                                            writeWithType(Boolean.class, param, out::value) ||
                                                    writeWithType(Character.class, param, out::value) ||
                                                    writeWithType(Byte.class, param, out::value) ||
                                                    writeWithType(Short.class, param, out::value) ||
                                                    writeWithType(Integer.class, param, out::value) ||
                                                    writeWithType(Long.class, param, out::value) ||
                                                    writeWithType(Float.class, param, out::value) ||
                                                    writeWithType(Double.class, param, out::value) ||
                                                    writeWithType(String.class, param, out::value)
                                    )) {
                                        out.nullValue();
                                    }
                                }
                            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                                log.error("", e);
                            }
                        });
                out.endObject();
            } catch (IntrospectionException e) {
                log.error("", e);
            }
        }

        public T read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL: {
                    in.nextNull();
                    return null;
                }
                case BEGIN_OBJECT: {
                    in.beginObject();

                    T r = null;

                    while (in.hasNext()) {
                        var name = in.nextName();
                        if (name.equals("value")) {
                            var value = in.nextString();
                            try {
                                r = Enum.valueOf(type, value);
                            } catch (IllegalStateException e) {
                                throw new BadRequestException(e.getMessage());
                            }
                        } else {
                            in.skipValue();
                        }
                    }

                    in.endObject();

                    return r;
                }
                case STRING: {
                    return Enum.valueOf(type, in.nextString());
                }
                default:
                    return null;
            }
        }
    }
}
