package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.impl.base.IntDetailColumn;

public class TempIntDetailColumn extends IntDetailColumn {

    private int[] data = {2, 4, 10, 1};

    public TempIntDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public int getInt(int pos) {
        return data[pos];
    }
}
