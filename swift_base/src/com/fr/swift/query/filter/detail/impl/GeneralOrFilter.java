package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class GeneralOrFilter implements DetailFilter {

    private List<FilterInfo> filterInfoList;
    private Segment segment;

    public GeneralOrFilter(List<FilterInfo> filterInfoList, Segment segment) {
        this.filterInfoList = filterInfoList;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        if (filterInfoList.size() == 0) {
            return new AllShowDetailFilter(segment).createFilterIndex();
        }
        final List<DetailFilter> filters = new ArrayList<DetailFilter>();
        for (FilterInfo filterInfo : filterInfoList) {
            filters.add(filterInfo.createDetailFilter(segment));
        }
        final BitMapOrHelper bitMapOrHelper = new BitMapOrHelper();
        for (DetailFilter filter : filters) {
            bitMapOrHelper.add(filter.createFilterIndex());
        }
        return bitMapOrHelper.compute();
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        return false;
    }
}
