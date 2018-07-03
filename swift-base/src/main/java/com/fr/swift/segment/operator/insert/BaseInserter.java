package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.utils.InserterUtils;
import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/6/15
 */
public abstract class BaseInserter {
    protected Segment segment;

    protected List<String> fields;

    private Map<Integer, MutableBitMap> nullIndices = new HashMap<Integer, MutableBitMap>();

    protected List<Column> columns = new ArrayList<Column>();

    BaseInserter(Segment segment) {
        this(segment, segment.getMetaData().getFieldNames());
    }

    public BaseInserter(Segment segment, List<String> fields) {
        this.fields = fields;
        this.segment = segment;
        init();
    }

    protected void init() {
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Column column = segment.getColumn(new ColumnKey(field));
            columns.add(column);
            nullIndices.put(i, BitMaps.newRoaringMutable());
        }
    }

    protected void putRow(int cursor, Row rowData) {
        for (int i = 0; i < fields.size(); i++) {
            DetailColumn detail = columns.get(i).getDetailColumn();
            if (InserterUtils.isBusinessNullValue(rowData.getValue(i))) {
                detail.put(cursor, null);
                nullIndices.get(i).add(cursor);
            } else {
                detail.put(cursor, rowData.getValue(i));
            }
        }
        segment.putRowCount(cursor + 1);
    }

    protected void putNullIndex() {
        boolean readable = CubeUtil.isReadable(segment);

        for (int i = 0; i < columns.size(); i++) {
            BitmapIndexedColumn bitmapIndex = columns.get(i).getBitmapIndex();
            ImmutableBitMap nullIndex = readable ? bitmapIndex.getNullIndex() : BitMaps.newRoaringMutable();
            bitmapIndex.putNullIndex(nullIndex.getOr(nullIndices.get(i)));
        }
    }

    protected void putSegmentInfo(int lastCursor, int cursor) {
        ImmutableBitMap allShowIndex = CubeUtil.isReadable(segment) ? segment.getAllShowIndex() : BitMaps.newRoaringMutable();
        allShowIndex = allShowIndex.getOr(new RangeBitmap(lastCursor, cursor));
        segment.putAllShowIndex(allShowIndex);
    }

    protected void release() {
        if (segment.getLocation().getStoreType() == StoreType.FINE_IO) {
            for (Column column : columns) {
                column.getDetailColumn().release();
                column.getBitmapIndex().release();
            }

            segment.release();
        }
    }

    public List<String> getFields() {
        return fields;
    }
}