package org.example;

public class Cell<T> {
    private T value;

    public Cell(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
