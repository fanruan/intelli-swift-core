package com.fr.swift.query.aggregator;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;

public class TempLongDetailColumn extends LongDetailColumn {
    private long[] data = {4, 2, 3, 0};

    public TempLongDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public long getLong(int pos) {
        return data[pos];
    }
}
