package org.example.expressions;

public class RangeExpression implements Expression {
    private final Reference start;
    private final Reference end;

    public RangeExpression(Reference start, Reference end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "( " + this.start + " : " + this.end + " )";
    }
}
