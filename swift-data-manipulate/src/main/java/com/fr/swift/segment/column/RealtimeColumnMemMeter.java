package com.fr.swift.segment.column;

import com.fr.swift.bitmap.impl.BitmapMemMeter;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaDataColumn;

/**
 * @author anchore
 * @date 2019/3/18
 */
public class RealtimeColumnMemMeter {

    public static long meter(Column<?> realtimeColumn, int rowCount, SwiftMetaDataColumn columnMeta) {
        if (realtimeColumn == null || realtimeColumn.getLocation().getStoreType().isPersistent()) {
            return 0;
        }

        int valueSize = getValueSize(columnMeta);

        long size = 0;

        // rowToVal
        size += rowCount * valueSize;

        DictionaryEncodedColumn<?> dictColumn = realtimeColumn.getDictionaryEncodedColumn();

        if (!dictColumn.isReadable()) {
            return size;
        }

        int dictSize = dictColumn.size();

        // indexAndId
        size += 2 * dictSize * 4;

        // idToVal
        size += dictSize * valueSize;

        // valToId
        size += dictSize * 4;

        BitmapIndexedColumn bitmapColumn = realtimeColumn.getBitmapIndex();

        if (bitmapColumn.isReadable()) {
            // valToRows
            for (int i = 0; i < dictSize; i++) {
                size += BitmapMemMeter.meter(bitmapColumn.getBitMapIndex(i));
            }
        }

        return size;
    }

    public static int getValueSize(SwiftMetaDataColumn columnMeta) {
        switch (ColumnTypeUtils.getColumnType(columnMeta)) {
            case DATE:
            case NUMBER:
                return 8;
            case STRING:
                return 2 * columnMeta.getPrecision();
            default:
                throw new IllegalArgumentException();
        }
    }
}