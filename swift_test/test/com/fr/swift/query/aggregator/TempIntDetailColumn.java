package com.fr.swift.query.aggregator;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;

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
