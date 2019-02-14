package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.DateColumn;
import com.fr.swift.segment.column.impl.DoubleColumn;
import com.fr.swift.segment.column.impl.LongColumn;
import com.fr.swift.segment.column.impl.StringColumn;
import com.fr.swift.segment.column.impl.empty.ImmutableNullColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2017/12/12
 */
@SwiftBean(name = "historySegment")
@SwiftScope("prototype")
public class HistorySegmentImpl extends MutableHistorySegment implements HistorySegment {

    private volatile ImmutableBitMap allShowBitMapCache;

    public HistorySegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        super(parent, meta);
    }

    @Override
    protected Column<?> newColumn(IResourceLocation location, ClassType classType) {
        if (!isReadable()) {
            return super.newColumn(location, classType);
        }

        return newNullableColumn(location, classType);
    }

    @Override
    public ImmutableBitMap getAllShowIndex() {
        if (allShowBitMapCache != null) {
            return allShowBitMapCache;
        }
        allShowBitMapCache = super.getAllShowIndex();
        return allShowBitMapCache;
    }

    private Column<?> newNullableColumn(IResourceLocation location, ClassType classType) {
        Column<?> column;
        switch (classType) {
            case INTEGER:
            case LONG:
                column = new LongColumn(location);
                if (!column.getDetailColumn().isReadable()) {
                    return ImmutableNullColumn.ofLong(location, readRowCount());
                }
                return column;
            case DOUBLE:
                column = new DoubleColumn(location);
                if (!column.getDetailColumn().isReadable()) {
                    return ImmutableNullColumn.ofDouble(location, readRowCount());
                }
                return column;
            case DATE:
                column = new DateColumn(location);
                if (!column.getDetailColumn().isReadable()) {
                    return ImmutableNullColumn.ofLong(location, readRowCount());
                }
                return column;
            case STRING:
                column = new StringColumn(location);
                if (!column.getDetailColumn().isReadable()) {
                    return ImmutableNullColumn.ofString(location, readRowCount());
                }
                return column;
            default:
                return Crasher.crash(String.format("cannot new correct column by class type: %s", classType));
        }
    }

    @Override
    public void release() {
        super.release();
        allShowBitMapCache = null;
    }

    private int readRowCount() {
        int rowCount = getRowCount();
        IoUtil.release(rowCountReader);
        rowCountReader = null;
        return rowCount;
    }
}