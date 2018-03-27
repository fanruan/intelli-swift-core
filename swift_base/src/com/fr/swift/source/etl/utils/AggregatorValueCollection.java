package com.fr.swift.source.etl.utils;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;

/**
 * Created by Handsome on 2017/12/7 0007 16:11
 */
public class AggregatorValueCollection implements AggregatorValue {
    private ImmutableBitMap bitMap;

    private Segment segment;

    private ColumnKey[] columnKey;

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public ColumnKey[] getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(ColumnKey[] columnKey) {
        this.columnKey = columnKey;
    }

    public void setBitMap(ImmutableBitMap bitMap) {
        this.bitMap = bitMap;
    }

    public ImmutableBitMap getBitMap() {
        return this.bitMap;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public Object calculateValue() {
        return null;
    }
}
