package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.query.filter.DetailFilterFactory;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.SwiftDetailFilterValue;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class GeneralOrFilter implements DetailFilter {

    private List<SwiftDetailFilterValue> filterValues;
    private Segment segment;

    public GeneralOrFilter(List<SwiftDetailFilterValue> filterValues, Segment segment) {
        this.filterValues = filterValues;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        final List<DetailFilter> filters = new ArrayList<DetailFilter>();
        for (SwiftDetailFilterValue filterValue : filterValues) {
            filters.add(DetailFilterFactory.createFilter(segment, filterValue));
        }
        final BitMapOrHelper bitMapOrHelper = new BitMapOrHelper();
        for (DetailFilter filter : filters) {
            bitMapOrHelper.add(filter.createFilterIndex());
        }
        return bitMapOrHelper.compute();
    }

    @Override
    public boolean matches(SwiftNode node) {
        return false;
    }
}
