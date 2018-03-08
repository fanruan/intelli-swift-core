package com.fr.swift.query.filter.info.value;

/**
 * Created by Lyon on 2018/3/7.
 */
public class SwiftDateInRangeFilterValue {

    private long start = Long.MIN_VALUE;
    private long end = Long.MAX_VALUE;

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
