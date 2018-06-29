package com.fr.swift.query.filter.info.value;

import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/2/27.
 */
public class SwiftNumberInRangeFilterValue {

    @JsonProperty
    private double min = Double.NEGATIVE_INFINITY;
    @JsonProperty
    private double max = Double.POSITIVE_INFINITY;
    @JsonProperty
    private boolean minIncluded = false;
    @JsonProperty
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwiftNumberInRangeFilterValue that = (SwiftNumberInRangeFilterValue) o;

        if (Double.compare(that.min, min) != 0) return false;
        if (Double.compare(that.max, max) != 0) return false;
        if (minIncluded != that.minIncluded) return false;
        return maxIncluded == that.maxIncluded;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(min);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(max);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (minIncluded ? 1 : 0);
        result = 31 * result + (maxIncluded ? 1 : 0);
        return result;
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
