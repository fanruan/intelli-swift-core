package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.RealtimeDateColumn;
import com.fr.swift.segment.column.RealtimeDoubleColumn;
import com.fr.swift.segment.column.RealtimeLongColumn;
import com.fr.swift.segment.column.RealtimeStringColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

/**
 * This class created on 2018-1-9 11:06:45
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@SwiftBean(name = "realtimeSegment")
@SwiftScope("prototype")
public class RealtimeSegmentImpl extends BaseSegment implements RealtimeSegment {
    public RealtimeSegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        super(parent, meta);
    }

    @Override
    public <T> Column<T> getColumn(ColumnKey key) {
        // realtime column不缓存column
        try {
            String name = key.getName();
            String columnId = meta.getColumnId(name);
            IResourceLocation child = location.buildChildLocation(columnId);
            Column<?> column = newColumn(child, ColumnTypeUtils.getClassType(meta.getColumn(name)));
            return (Column<T>) column;
        } catch (SwiftMetaDataColumnAbsentException e) {
            if (key.getRelation() != null) {
                return createRelationColumn(key);
            }
            SwiftLoggers.getLogger().error("getColumn failed", e);
            return null;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getColumn failed", e);
            return null;
        }
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