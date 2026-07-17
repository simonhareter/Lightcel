package org.example.util;

import org.example.expressions.CellReference;

public record RangeValue(CellReference start, CellReference end) implements Value {

}
