package org.example;

public class Table<T> {
    private String name;
    private int rows;
    private int cols;
    private Cell<T>[][] cells;

    public Table(String name) {
        this.name = name;
        this.rows = 1000;
        this.cols = 26;
        this.cells = new Cell[rows][cols];
    }

    public void setCell(int row, int col, T value) {
        this.cells[row][col] = new Cell<T>(value);
    }
} 
  
