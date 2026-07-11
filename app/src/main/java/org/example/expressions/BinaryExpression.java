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

    public Token getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }

}
