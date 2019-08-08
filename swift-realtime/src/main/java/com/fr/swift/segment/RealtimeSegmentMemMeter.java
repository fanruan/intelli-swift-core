package com.fr.swift.segment;

import com.fr.swift.bitmap.impl.BitmapMemMeter;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.RealtimeColumnMemMeter;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author anchore
 * @date 2019/3/19
 */
public class RealtimeSegmentMemMeter {

    public static long meter(RealtimeSegment seg) {
        if (seg == null || !seg.isReadable()) {
            return 0;
        }

        long size = 0;

        int rowCount = seg.getRowCount();

        SwiftMetaData metaData = seg.getMetaData();
        for (String fieldName : metaData.getFieldNames()) {
            try {
                Column<Object> column = seg.getColumn(new ColumnKey(fieldName));
                size += RealtimeColumnMemMeter.meter(column, rowCount, metaData.getColumn(fieldName));
            } catch (SwiftMetaDataException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }

        size += BitmapMemMeter.meter(seg.getAllShowIndex());

        return size;
    }

}