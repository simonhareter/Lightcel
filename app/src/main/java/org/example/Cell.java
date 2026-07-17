package org.example;

import org.example.util.Value;

public class Cell {
    private Object value;
    private Value evaluatedValue;
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

    public Value getEvaluatedValue() {
        return evaluatedValue;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setEvaluatedValue(Value evaluatedValue) {
        this.evaluatedValue = evaluatedValue;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public CellType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "" + this.value;
    }

}
