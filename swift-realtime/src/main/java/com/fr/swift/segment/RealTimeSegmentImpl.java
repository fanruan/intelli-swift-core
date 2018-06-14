package com.fr.swift.segment;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.RealtimeDateColumn;
import com.fr.swift.segment.column.RealtimeDoubleColumn;
import com.fr.swift.segment.column.RealtimeLongColumn;
import com.fr.swift.segment.column.RealtimeStringColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

/**
 * This class created on 2018-1-9 11:06:45
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealTimeSegmentImpl extends BaseSegment implements RealTimeSegment {
    public RealTimeSegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        super(parent, meta);
    }

    @Override
    protected Column<?> newColumn(IResourceLocation location, ClassType classType) {
        switch (classType) {
            case INTEGER:
            case LONG:
                return new RealtimeLongColumn(location);
            case DOUBLE:
                return new RealtimeDoubleColumn(location);
            case DATE:
                return new RealtimeDateColumn(location);
            case STRING:
                return new RealtimeStringColumn(location);
            default:
                return Crasher.crash(String.format("cannot new correct column by class type: %s", classType));
        }
    }
}