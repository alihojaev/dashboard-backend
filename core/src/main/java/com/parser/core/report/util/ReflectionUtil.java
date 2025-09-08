package com.parser.core.report.util;

import java.lang.reflect.Field;

public class ReflectionUtil {
    public static Object getFieldValue(Object obj, String fieldPath) {
        if (obj == null || fieldPath == null) return null;
        String[] parts = fieldPath.split("\\.");
        Object current = obj;
        for (String part : parts) {
            if (current == null) return null;
            try {
                Field field = getField(current.getClass(), part);
                if (field == null) return null;
                field.setAccessible(true);
                current = field.get(current);
            } catch (Exception e) {
                return null;
            }
        }
        return current;
    }

    private static Field getField(Class<?> clazz, String name) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
} 