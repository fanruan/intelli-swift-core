package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class GeneralOrFilter implements DetailFilter {

    private List<FilterInfo> filterValues;
    private Segment segment;

    public GeneralOrFilter(List<FilterInfo> filterValues, Segment segment) {
        this.filterValues = filterValues;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        final BitMapOrHelper bitMapOrHelper = new BitMapOrHelper();
        for (FilterInfo filterValue : filterValues) {
            DetailFilter filter = filterValue.createDetailFilter(segment);
            bitMapOrHelper.add(filter.createFilterIndex());
        }
        return bitMapOrHelper.compute();
    }

    @Override
    public boolean matches(SwiftNode node) {
        return false;
    }
}
