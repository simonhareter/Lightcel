package org.example;

import org.example.util.exceptions.InvalidReferenceException;
import org.example.util.exceptions.TableFullException;

public class Table {
    public static final int MAX_ROW = 100_000;
    public static final int MAX_COL = 10_000;
    public static final long MAX_CELLS = 1_000_000;

    private int maxInsertedRow;
    private int maxInsertedCol;
    private long cellCounter;
    private Cell[][] cells;

    public Table() {
        this.cells = new Cell[MAX_ROW][MAX_COL];
        this.maxInsertedRow = 0;
        this.maxInsertedCol = 0;
        this.cellCounter = 0;
    }

    public void setCell(int row, int col, String value, CellType type) {
        if (this.cellCounter == MAX_CELLS) {
            throw new TableFullException("Can't add any more cells. Hit the limit of " + MAX_CELLS);
        }

        if (row > MAX_ROW || col > MAX_COL) {
            throw new InvalidReferenceException("Outside of " + MAX_ROW + " and " + MAX_COL + " limits!");
        }

        if (row > getMaxInsertedRow()) {
            setMaxInsertedRow(row);
        }

        if (col > getMaxInsertedCol()) {
            setMaxInsertedCol(col);
        }

        Object parsedValue = switch (type) {
            case EMPTY -> null;
            case NUMBER -> Double.parseDouble(value);
            case BOOLEAN -> Boolean.parseBoolean(value);
            case FORMULA, STRING -> value;
        };

        this.cellCounter++;
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
}
