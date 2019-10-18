package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.utils.InserterUtils;
import com.fr.swift.source.Row;

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

    protected Map<String, MutableBitMap> nullIndices = new HashMap<>();

    protected Map<String, Column<Object>> columns = new HashMap<>();

    BaseInserter(Segment segment) {
        this(segment, segment.getMetaData().getFieldNames());
    }

    public BaseInserter(Segment segment, List<String> fields) {
        this.fields = fields;
        this.segment = segment;
        initColumns();
    }

    private void initColumns() {
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Column<Object> column = segment.getColumn(new ColumnKey(field));
            columns.put(field, column);
            nullIndices.put(field, BitMaps.newRoaringMutable());
        }
    }

    protected void putRow(int cursor, Row rowData) {
        for (int i = 0; i < fields.size(); i++) {
            DetailColumn<Object> detail = columns.get(fields.get(i)).getDetailColumn();
            if (InserterUtils.isBusinessNullValue(rowData.getValue(i))) {
                detail.put(cursor, null);
                nullIndices.get(fields.get(i)).add(cursor);
            } else {
                detail.put(cursor, rowData.getValue(i));
            }
        }
    }

    protected void putNullIndex(int cursor) {
        boolean readable = CubeUtil.isReadable(segment);

        for (int i = 0; i < columns.size(); i++) {
            BitmapIndexedColumn bitmapIndex = columns.get(fields.get(i)).getBitmapIndex();
            ImmutableBitMap nullIndex;
            try {
                nullIndex = readable ? bitmapIndex.getNullIndex() : BitMaps.newRoaringMutable();
            } catch (Exception e) {
                nullIndex = BitMaps.newRoaringMutable();
            }
            MutableBitMap newNullIndex = nullIndices.get(fields.get(i));
            // bitmap限制在rowCount内
            newNullIndex.and(RangeBitmap.of(0, cursor));
            newNullIndex.or(nullIndex);
            bitmapIndex.putNullIndex(newNullIndex);
        }
    }

    protected void putSegmentInfo(int lastCursor, int cursor) {
        ImmutableBitMap allShowIndex;
        try {
            allShowIndex = CubeUtil.isReadable(segment) ? segment.getAllShowIndex() : AllShowBitMap.of(0);
        } catch (Exception e) {
            allShowIndex = AllShowBitMap.of(0);
        }

        if (allShowIndex.isFull()) {
            allShowIndex = AllShowBitMap.of(cursor);
        } else {
            // bitmap限制在rowCount内
            allShowIndex = allShowIndex.getAnd(new RangeBitmap(0, cursor))
                    .getOr(new RangeBitmap(lastCursor, cursor));
        }
        segment.putRowCount(cursor);
        segment.putAllShowIndex(allShowIndex);
    }

    protected void release() {
        SegmentUtils.releaseHisSeg(segment);
    }

    public List<String> getFields() {
        return fields;
    }
}