package com.fr.swift.segment.column;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/6/13
 */
public class RealtimeDoubleColumn extends BaseRealtimeColumn<Double> {
    public RealtimeDoubleColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    protected ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.DOUBLE;
    }

    @Override
    protected Comparator<Double> getComparator() {
        return Comparators.asc();
    }
}