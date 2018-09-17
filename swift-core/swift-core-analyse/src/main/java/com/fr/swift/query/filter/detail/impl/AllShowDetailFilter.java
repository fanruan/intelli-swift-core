package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

/**
 * Created by pony on 2018/3/23.
 */
public class AllShowDetailFilter implements DetailFilter {
    private Segment segment;

    public AllShowDetailFilter(Segment segment) {
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        return segment.getAllShowIndex();
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        return true;
    }
}
