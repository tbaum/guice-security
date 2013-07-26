package com.google.inject.extensions.security;

import java.lang.reflect.Field;

public class TestHelper {
    static <T, R> R field(T service, String fieldName, Class<T> clazz) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (R) field.get(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
