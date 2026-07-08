package org.example.util;

public class Functions {

    public static double sum(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum;
    }

    public static double average(double[] values) {
        return sum(values) / values.length;
    }

    public static double min(double[] values) {
        double min = 0;
        for (double value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static double max(double[] values) {
        double max = 0;
        for (double value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int count(double[] values) {
        return values.length;
    }
}
