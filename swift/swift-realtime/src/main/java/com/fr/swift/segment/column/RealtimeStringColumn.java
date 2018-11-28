package com.fr.swift.segment.column;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.ColumnTypeConstants;

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