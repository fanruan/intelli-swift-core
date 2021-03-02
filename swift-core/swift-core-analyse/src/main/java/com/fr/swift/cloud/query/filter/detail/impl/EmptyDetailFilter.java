package com.fr.swift.cloud.query.filter.detail.impl;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;

public class EmptyDetailFilter implements DetailFilter {

    @Override
    public ImmutableBitMap createFilterIndex() {
        return BitMaps.EMPTY_IMMUTABLE;
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        return false;
    }
}