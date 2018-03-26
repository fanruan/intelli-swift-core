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

    public long getEnd() {
        return endIncluded;
    }

    public void setStart(long start) {
        this.startIncluded = start;
    }

    public void setEnd(long end) {
        this.endIncluded = end;
    }
}
