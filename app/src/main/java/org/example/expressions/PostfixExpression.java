package org.example.expressions;

import org.example.Token;

public class PostfixExpression implements Expression {
    private final Token operator;
    private final Expression left;

    public PostfixExpression(Expression left, Token operator) {
        this.left = left;
        this.operator = operator;
    }

    public Token getOperator() {
        return this.operator;
    }

    public Expression getLeft() {
        return this.left;
    }

    @Override
    public String toString() {
        return this.left + operator.getValue();
    }

}
