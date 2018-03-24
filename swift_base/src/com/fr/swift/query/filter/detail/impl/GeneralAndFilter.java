package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class GeneralAndFilter implements DetailFilter {

    private List<FilterInfo> filterInfoList;
    private Segment segment;

    public GeneralAndFilter(List<FilterInfo> filterInfoList, Segment segment) {
        this.filterInfoList = filterInfoList;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        final List<DetailFilter> filters = new ArrayList<DetailFilter>();
        for (FilterInfo filterInfo : filterInfoList) {
            filters.add(filterInfo.createDetailFilter(segment));
        }
        ImmutableBitMap bitMap = BitMaps.newAllShowBitMap(segment.getRowCount());
        for (DetailFilter filter : filters) {
            bitMap = bitMap.getAnd(filter.createFilterIndex());
        }
        return bitMap;
    }

    @Override
    public boolean matches(SwiftNode node) {
        return false;
    }
}
