package com.fr.swift.query.filter.info.value;

/**
 * Created by Lyon on 2018/2/27.
 */
public class SwiftNumberInRangeFilterValue {

    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private boolean minIncluded = false;
    private boolean maxIncluded = false;

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isMinIncluded() {
        return minIncluded;
    }

    public boolean isMaxIncluded() {
        return maxIncluded;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMinIncluded(boolean minIncluded) {
        this.minIncluded = minIncluded;
    }

    public void setMaxIncluded(boolean maxIncluded) {
        this.maxIncluded = maxIncluded;
    }
}
