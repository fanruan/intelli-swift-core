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

    private List<DetailFilter> filters;
    private Segment segment;

    public GeneralAndFilter(List<FilterInfo> filterInfoList, Segment segment) {
        this.segment = segment;
        this.filters = init(filterInfoList);
    }

    private List<DetailFilter> init(List<FilterInfo> filterInfoList) {
        List<DetailFilter> detailFilters = new ArrayList<DetailFilter>();
        if (filterInfoList.size() == 0) {
            detailFilters.add(new AllShowDetailFilter(segment));
            return detailFilters;
        }
        for (FilterInfo filterInfo : filterInfoList) {
//            filterInfo可能为空
//            if(filterInfo != null) {
            detailFilters.add(filterInfo.createDetailFilter(segment));
//            }
        }
        return detailFilters;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        ImmutableBitMap bitMap = BitMaps.newAllShowBitMap(segment.getRowCount());
        for (DetailFilter filter : filters) {
            bitMap = bitMap.getAnd(filter.createFilterIndex());
        }
        return bitMap;
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        for (DetailFilter filter : filters) {
            if (!filter.matches(node, targetIndex)) {
                return false;
            }
        }
        return true;
    }
}
