package org.example.expressions;

public class RangeExpression extends Expression {
    private final CellReference start;
    private final CellReference end;

    public RangeExpression(CellReference start, CellReference end) {
        this.start = start;
        this.end = end;
    }

}
