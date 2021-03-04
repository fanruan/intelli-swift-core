package com.fr.swift.cloud.segment.column;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/6/13
 */
public class RealtimeStringColumn extends BaseRealtimeColumn<String> {
    public RealtimeStringColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    protected ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.STRING;
    }

    @Override
    protected Comparator<String> getComparator() {
        return Comparators.STRING_ASC;
    }
}