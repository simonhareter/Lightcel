package org.example.expressions;

public interface Reference extends Expression {
    RangeBounds toBounds(int maxInserted);
}
