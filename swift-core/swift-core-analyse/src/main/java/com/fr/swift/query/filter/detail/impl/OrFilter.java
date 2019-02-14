package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.FasterAggregation;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/3/16.
 */
public class OrFilter implements DetailFilter {

    private List<DetailFilter> filters;

    public OrFilter(List<DetailFilter> filters) {
        this.filters = filters;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        final List<ImmutableBitMap> bitMapOrHelper = new ArrayList<ImmutableBitMap>();
        for (DetailFilter filter : filters) {
            bitMapOrHelper.add(filter.createFilterIndex());
        }
        return FasterAggregation.or(bitMapOrHelper);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        for (DetailFilter filter : filters) {
            if (filter.matches(node, targetIndex, converter)) {
                return true;
            }
        }
        return false;
    }
}
