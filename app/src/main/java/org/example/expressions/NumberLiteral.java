package org.example.expressions;

public class NumberLiteral implements Expression {
    private double value;

    public NumberLiteral(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
