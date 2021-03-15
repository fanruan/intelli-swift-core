package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.impl.base.DoubleDetailColumn;

public class TempDoubleDetailColumn extends DoubleDetailColumn {

    double[] data = {1.2, 3.7, 9.8, 4.3};

    public TempDoubleDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public double getDouble(int pos) {
        return data[pos];
    }
}