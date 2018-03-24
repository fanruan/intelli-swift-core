package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class GeneralAndFilter implements DetailFilter {

    private List<FilterInfo> filterValues;
    private Segment segment;

    public GeneralAndFilter(List<FilterInfo> filterValues, Segment segment) {
        this.filterValues = filterValues;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        ImmutableBitMap bitMap = BitMaps.newAllShowBitMap(segment.getRowCount());
        for (FilterInfo filterValue : filterValues) {
            DetailFilter filter = filterValue.createDetailFilter(segment);
            bitMap = bitMap.getAnd(filter.createFilterIndex());
        }
        return bitMap;
    }

    @Override
    public boolean matches(SwiftNode node) {
        return false;
    }
}
