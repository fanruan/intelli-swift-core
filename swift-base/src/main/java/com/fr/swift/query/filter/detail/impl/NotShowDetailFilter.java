package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.SwiftNode;

public class NotShowDetailFilter implements DetailFilter {

    @Override
    public ImmutableBitMap createFilterIndex() {
        return BitMaps.EMPTY_IMMUTABLE;
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        return false;
    }
}