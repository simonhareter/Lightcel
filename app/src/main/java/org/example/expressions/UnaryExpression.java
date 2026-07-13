package org.example.expressions;

import org.example.Token;

public class UnaryExpression implements Expression {
    private final Token operator;
    private final Expression right;

    public UnaryExpression(Token operator, Expression right) {
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "( " + this.operator.getValue() + this.right + " )";
    }

    public Token getOperator() {
        return this.operator;
    }

    public Expression getRight() {
        return this.right;
    }
}
