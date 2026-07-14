package org.example.util;

import org.example.Cell;
import org.example.Table;

public class Functions {
    private final Table table;

    public Functions(Table table) {
        this.table = table;
    }

    public static double sum(Object[] values) {
        double sum = 0;
        for (Object value : values) {
            if (value instanceof Range range) {
                for (Cell cell : table.getRange(range.start(), range.end())) {
                    sum += (double) cell.getValue();
                }
            } else {
                sum += (double) value;
            }
        }
        return sum;
    }

    public static double average(Object[] values) {
        return sum(values) / values.length;
    }

    public static double min(Object[] values) {
        double min = 0;
        for (Object value : values) {
            if ((double) value < min) {
                min = (double) value;
            }
        }
        return min;
    }

    public static double max(Object[] values) {
        double max = 0;
        for (Object value : values) {
            if ((double) value > max) {
                max = (double) value;
            }
        }
        return max;
    }

    public static int count(Object[] values) {
        return values.length;
    }
}
