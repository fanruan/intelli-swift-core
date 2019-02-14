package com.fr.swift.query.aggregator;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;

public class TempDoubleDetailColumn extends DoubleDetailColumn{

    double[] data = {1.2, 3.7, 9.8, 4.3};

    public TempDoubleDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public double getDouble(int pos) {
        return data[pos];
    }
}