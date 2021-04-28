package com.fr.swift.cloud.segment.column;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author lucifer
 * @date 2019/6/19
 * @description
 * @since swift 1.0
 */
public class RealtimeIntColumn extends BaseRealtimeColumn<Integer> {

    public RealtimeIntColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    protected ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.INTEGER;
    }

    @Override
    protected Comparator<Integer> getComparator() {
        return Comparators.asc();
    }
}