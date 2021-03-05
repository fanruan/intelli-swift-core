package com.fr.swift.cloud.segment.column;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.ColumnTypeConstants;

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