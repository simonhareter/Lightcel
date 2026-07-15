package org.example.expressions;

public class CellReference implements Reference {
    private final int row;
    private final int column;

    public CellReference(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public RangeBounds toBounds(int maxInsertedRow) {
        return new RangeBounds(this.row, this.row, this.column, this.column);
    }

    @Override
    public String toString() {
        return "Cell(r=" + row + ", c=" + column + ")";
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
