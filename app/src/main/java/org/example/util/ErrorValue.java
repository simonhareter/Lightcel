package org.example.util;

public enum ErrorValue implements Value {
    REF("#REF!"),
    VALUE("#VALUE!"),
    DIV0("#DIV0!"),
    CIRCULAR_REF("CIRCULAR_REF");

    private final String text;

    ErrorValue(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
