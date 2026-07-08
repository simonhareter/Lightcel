package org.example.expressions;

public class CellReference extends Expression {
    private final int row;
    private final int column;

    public CellReference(int row, int column) {
        this.row = row;
        this.column = column;
    }

}
