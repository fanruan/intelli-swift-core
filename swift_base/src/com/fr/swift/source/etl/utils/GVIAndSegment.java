package com.fr.swift.source.etl.utils;

import com.fr.swift.segment.Segment;

import java.util.Comparator;

/**
 * Created by Handsome on 2017/12/17 0017 20:36
 */
public class GVIAndSegment {
    private int row;
    private Segment segment;
    private Object[] values;

    public GVIAndSegment(int row, Segment segment, Object[] values) {
        this.row = row;
        this.segment = segment;
        this.values = values;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public int compareTo(GVIAndSegment o, Comparator[] comparators) {
        if (o == null) {
            return -1;
        }
        for (int i = 0; i < values.length; i++) {
            int result = comparators[i].compare(String.valueOf(values[i]), String.valueOf(o.values[i]));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
