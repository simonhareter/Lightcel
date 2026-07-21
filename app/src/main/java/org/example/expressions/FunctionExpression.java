package org.example.expressions;

import java.util.List;
import java.util.stream.Collectors;
import org.example.util.FunctionType;

public class FunctionExpression implements Expression {
    private final FunctionType function;
    private final List<Expression> arguments;

    public FunctionExpression(FunctionType function, List<Expression> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public FunctionType getFunction() {
        return this.function;
    }

    public List<Expression> getArguments() {
        return this.arguments;
    }

    @Override
    public String toString() {
        return this.function + "( "
                + this.arguments.stream().map(Expression::toString).collect(Collectors.joining(", ")) + " )";
    }
}
