package org.example;

public class Table {
    private int rows;
    private int cols;
    private int maxInsertedRow;
    private int maxInsertedCol;
    private Cell[][] cells;

    public Table() {
        this.rows = 1000;
        this.cols = 26;
        this.cells = new Cell[rows][cols];
        this.maxInsertedRow = 0;
        this.maxInsertedCol = 0;
    }

    public void setCell(int row, int col, String value, CellType type) {
        Object parsedValue = switch (type) {
            case EMPTY -> null;
            case NUMBER -> Double.parseDouble(value);
            case BOOLEAN -> Boolean.parseBoolean(value);
            case FORMULA, STRING -> value;
        };

        this.cells[row][col] = new Cell(parsedValue, type);
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void setMaxInsertedRow(int newMaxRow) {
        this.maxInsertedRow = newMaxRow;
    }

    public int getMaxInsertedRow() {
        return maxInsertedRow;
    }

    public void setMaxInsertedCol(int newMaxCol) {
        this.maxInsertedCol = newMaxCol;
    }

    public int getMaxInsertedCol() {
        return maxInsertedCol;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
