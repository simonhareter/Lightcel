package org.example.expressions;

public class RangeBounds {
    private int startRow;
    private int endRow;
    private int startCol;
    private int endCol;

    public RangeBounds(int startRow, int endRow, int startCol, int endCol) {
        this.startRow = startRow;
        this.endRow = endRow;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    public int getStartRow() {
        return this.startRow;
    }

    public int getEndRow() {
        return this.endRow;
    }

    public int getStartCol() {
        return this.startCol;
    }

    public int getEndCol() {
        return this.endCol;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    @Override
    public String toString() {
        return "RangeBounds [startRow=" + this.startRow + ", endRow=" + this.endRow + ", startCol=" + this.startCol
                + ", endCol="
                + this.endCol + "]";
    }
}
