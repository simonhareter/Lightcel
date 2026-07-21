package org.example.util;

public record StringValue(String value) implements Value {

    @Override
    public String toString() {
        return this.value;
    }
}
