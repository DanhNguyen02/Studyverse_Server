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

    public static boolean safeConvertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String stringValue) {
            if ("true".equalsIgnoreCase(stringValue)) {
                return true;
            } else if ("false".equalsIgnoreCase(stringValue)) {
                return false;
            } else {
                throw new IllegalArgumentException("String value cannot be converted to boolean: " + value);
            }
        } else {
            throw new IllegalArgumentException("Value cannot be converted to boolean: " + value);
        }
    }
}
