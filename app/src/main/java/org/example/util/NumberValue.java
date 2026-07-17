package org.example.util;

public record NumberValue(double value) implements Value {

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
