package org.example.expressions;

public class ColumnReference implements Reference {
    private int column;

    public ColumnReference(int column) {
        this.column = column;
    }

    @Override
    public RangeBounds toBounds(int maxInsertedRow) {
        return new RangeBounds(0, maxInsertedRow, this.column, this.column);
    }

    public int getColumn() {
        return column;
    }
}
