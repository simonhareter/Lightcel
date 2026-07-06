package org.example;

public class Token {
    private TokenType type;
    private String value;

    public Token() {

    }

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token:\n");
        sb.append("  Type: " + this.type + "\n");
        sb.append("  Value: " + this.value);
        return sb.toString();
    }

    public void fillToken(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }
}
