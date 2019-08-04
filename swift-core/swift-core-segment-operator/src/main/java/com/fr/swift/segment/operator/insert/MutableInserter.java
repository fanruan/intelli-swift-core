package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author lucifer
 * @date 2019-07-29
 * @description
 * @since advanced swift 1.0
 */
public class MutableInserter extends SwiftInserter {

    public MutableInserter(Segment segment, boolean append) {
        super(segment, append);
    }

    public static MutableInserter ofOverwriteMode(Segment seg) {
        return new MutableInserter(seg, false);
    }

    public void refreshMetadata(SwiftMetaData metaData) {
        this.fields = metaData.getFieldNames();
        ((MutableCacheColumnSegment) segment).refreshMetadata(metaData);
        refreshColumn();
    }

    private void refreshColumn() {
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            if (!columns.containsKey(field)) {
                Column<Object> column = segment.getColumn(new ColumnKey(field));
                columns.put(field, column);
                MutableBitMap nullMap = BitMaps.newRoaringMutable();
                nullMap.or(BitMaps.newRangeBitmap(0, cursor));
                nullIndices.put(field, nullMap);
                for (int j = 0; j < cursor; j++) {
                    column.getDetailColumn().put(j, null);
                }
            }
        }

    }
}
