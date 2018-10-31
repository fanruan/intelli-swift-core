package com.fr.swift.segment.column;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeConstants.ClassType;

import java.util.Comparator;
import java.util.Date;

/**
 * @author anchore
 * @date 2018/6/13
 */

public class RealtimeDateColumn extends BaseRealtimeColumn<Date> {

    public RealtimeDateColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    protected ColumnTypeConstants.ClassType getType() {
        return ClassType.DATE;
    }

    @Override
    protected Comparator<Date> getComparator() {
        return Comparators.asc();
    }
}