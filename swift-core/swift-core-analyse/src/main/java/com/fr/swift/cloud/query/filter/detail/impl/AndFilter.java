package com.fr.swift.cloud.query.filter.detail.impl;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;

import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class AndFilter implements DetailFilter {

    private List<DetailFilter> filters;

    public AndFilter(List<DetailFilter> filters) {
        this.filters = filters;
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
        return bitMap == null ? BitMaps.EMPTY_IMMUTABLE : bitMap;
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
