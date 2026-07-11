package org.example;

public enum TokenType {
    EQUALS,
    FORMULA_START,
    OPEN_PARENTHESES,
    CLOSING_PARENTHESES,
    COMMA,
    COLON,
    CELL,
    IDENTIFIER,
    STRING,
    EMPTY_STRING,
    ERROR_UNTERMINATED_STRING,
    MATH_OPERATOR,
    COMPARISON_OPERATOR,
    LOGICAL_OPERATOR,
    CONCATENATION_OPERATOR,
    PERCENTAGE_OPERATOR,
    NUMBER,
    EOF;

    public boolean matches(TokenType... types) {
        for (TokenType type : types) {
            if (this == type) {
                return true;
            }
        }
        return false;
    }
}
