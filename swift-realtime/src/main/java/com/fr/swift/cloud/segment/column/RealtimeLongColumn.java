package com.fr.swift.cloud.segment.column;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/6/7
 */
public class RealtimeLongColumn extends BaseRealtimeColumn<Long> {
    public RealtimeLongColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    protected ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.LONG;
    }

    @Override
    protected Comparator<Long> getComparator() {
        return Comparators.asc();
    }
}