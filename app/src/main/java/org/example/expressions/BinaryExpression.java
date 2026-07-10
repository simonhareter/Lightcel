package org.example.expressions;

import org.example.Token;

public class BinaryExpression implements Expression {
    private final Token operator;
    private final Expression left;
    private final Expression right;

    public BinaryExpression(Expression right, Token operator, Expression left) {
        this.right = right;
        this.operator = operator;
        this.left = left;
    }
}
