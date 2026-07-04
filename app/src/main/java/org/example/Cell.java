package org.example;

public class Cell {
    private Object value;
    private CellType type;

    public Cell(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setCellType(CellType newType) {
        this.type = newType;
    }

    public CellType getCellType() {
        return type;
    }

}
