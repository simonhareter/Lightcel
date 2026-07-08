package org.example.expressions;

import java.util.List;
import org.example.util.FunctionType;

public class FunctionExpression extends Expression {
    private final FunctionType function;
    private final List<Expression> arguments;

    public FunctionExpression(FunctionType function, List<Expression> arguments) {
        this.function = function;
        this.arguments = arguments;
    }
}
