package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class AndFilter implements DetailFilter {

    private List<DetailFilter> filters;
    private Segment segment;

    public AndFilter(List<FilterInfo> filterInfoList, Segment segment) {
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
            detailFilters.add(filterInfo.createDetailFilter(segment));
        }
        return detailFilters;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        ImmutableBitMap bitMap = null;
        for (DetailFilter filter : filters) {
            if (bitMap == null) {
                bitMap = filter.createFilterIndex();
                continue;
            }
            bitMap = bitMap.getAnd(filter.createFilterIndex());
        }
        return bitMap;
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        for (DetailFilter filter : filters) {
            if (!filter.matches(node, targetIndex, converter)) {
                return false;
            }
        }
        return true;
    }
}
