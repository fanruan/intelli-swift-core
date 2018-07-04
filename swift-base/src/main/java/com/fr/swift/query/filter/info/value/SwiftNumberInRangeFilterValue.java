package com.fr.swift.query.filter.info.value;

/**
 * Created by Lyon on 2018/2/27.
 */
public class SwiftNumberInRangeFilterValue {

    private Number min = Double.NEGATIVE_INFINITY;
    private Number max = Double.POSITIVE_INFINITY;
    private boolean minIncluded = false;
    private boolean maxIncluded = false;

    public Number getMin() {
        return min;
    }

    public void setMin(Number min) {
        this.min = min;
    }

    public Number getMax() {
        return max;
    }

    public void setMax(Number max) {
        this.max = max;
    }

    public boolean isMinIncluded() {
        return minIncluded;
    }

    public void setMinIncluded(boolean minIncluded) {
        this.minIncluded = minIncluded;
    }

    public boolean isMaxIncluded() {
        return maxIncluded;
    }

    public void setMaxIncluded(boolean maxIncluded) {
        this.maxIncluded = maxIncluded;
    }

    @Override
    public String toString() {
        return "SwiftNumberInRangeFilterValue{" +
                "min=" + min +
                ", max=" + max +
                ", minIncluded=" + minIncluded +
                ", maxIncluded=" + maxIncluded +
                '}';
    }
}
