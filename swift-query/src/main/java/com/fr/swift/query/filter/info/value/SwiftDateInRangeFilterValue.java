package com.fr.swift.query.filter.info.value;

/**
 * Created by Lyon on 2018/3/7.
 */
public class SwiftDateInRangeFilterValue {

    private long startIncluded = Long.MIN_VALUE;
    private long endIncluded = Long.MAX_VALUE;

    public long getStart() {
        return startIncluded;
    }

    public void setStart(long start) {
        this.startIncluded = start;
    }

    public long getEnd() {
        return endIncluded;
    }

    public void setEnd(long end) {
        this.endIncluded = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwiftDateInRangeFilterValue that = (SwiftDateInRangeFilterValue) o;

        if (startIncluded != that.startIncluded) return false;
        return endIncluded == that.endIncluded;
    }

    @Override
    public int hashCode() {
        int result = (int) (startIncluded ^ (startIncluded >>> 32));
        result = 31 * result + (int) (endIncluded ^ (endIncluded >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SwiftDateInRangeFilterValue{" +
                "startIncluded=" + startIncluded +
                ", endIncluded=" + endIncluded +
                '}';
    }
}
