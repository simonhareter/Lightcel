package org.example.expressions;

public class RowReference implements Reference {
    private int row;

    public RowReference(int row) {
        this.row = row;
    }

    @Override
    public RangeBounds toBounds(int maxInsertedCol) {
        return new RangeBounds(this.row, this.row, 0, maxInsertedCol);
    }

    public int getRow() {
        return this.row;
    }

    @Override
    public String toString() {
        return String.valueOf(this.row);
    }
}
