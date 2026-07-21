package org.example;

public enum CellType {
    NUMBER,
    STRING,
    BOOLEAN,
    FORMULA,
    EMPTY;

    public static CellType getTypeFromString(String value) {
        if (value.isEmpty()) {
            return EMPTY;
        } else if (isNumber(value)) {
            return NUMBER;
        } else if (value.equals("TRUE") || value.equals("FALSE")) {
            return BOOLEAN;
        } else if (value.startsWith("=")) {
            return FORMULA;
        } else {
            return STRING;
        }
    }

    public static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
