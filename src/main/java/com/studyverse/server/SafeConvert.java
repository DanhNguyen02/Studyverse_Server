package com.studyverse.server;

public class SafeConvert {
    public static int safeConvertToInt(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else {
            throw new IllegalArgumentException("Value cannot be converted to int: " + value);
        }
    }
}
