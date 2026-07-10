package org.example.expressions;

public class StringLiteral implements Expression {
    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }
}
