package org.example;

public class Token {
    private TokenType type;
    private String value;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token: Value = " + value + " , " + "Type = " + type;
    }
}
