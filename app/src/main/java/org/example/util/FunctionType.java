package org.example.util;

public enum FunctionType {
    SUM,
    AVERAGE,
    MIN,
    MAX,
    COUNT;

    public static FunctionType findByName(String name) {
        for (FunctionType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isFunctionType(String name) {
        for (FunctionType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
