package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.Deleter;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractDeleter implements Deleter {

    protected Segment segment;

    private DictionaryEncodedColumn dictionaryEncodedColumn;
    private BitmapIndexedColumn bitmapIndexedColumn;
    private ImmutableBitMap allShowIndex;

    public AbstractDeleter(Segment segment) throws SwiftMetaDataException {
        this.segment = segment;

        String allotColumn = segment.getMetaData().getColumnName(1);
        dictionaryEncodedColumn = segment.getColumn(new ColumnKey(allotColumn)).getDictionaryEncodedColumn();
        bitmapIndexedColumn = segment.getColumn(new ColumnKey(allotColumn)).getBitmapIndex();
        allShowIndex = segment.getAllShowIndex();
    }

    @Override
    public boolean deleteData(List<Row> rowList) throws Exception {
        for (Row row : rowList) {
            int decreaseIndex = dictionaryEncodedColumn.getIndex(row.getValue(0));
            ImmutableBitMap bitMap = bitmapIndexedColumn.getBitMapIndex(decreaseIndex);
            allShowIndex = allShowIndex.getAndNot(bitMap);
            segment.putAllShowIndex(allShowIndex);
        }
        release();
        return true;
    }

    @Override
    public boolean deleteData(SwiftResultSet swiftResultSet) throws Exception {
        while (swiftResultSet.next()) {
            Row row = swiftResultSet.getRowData();
            int decreaseIndex = dictionaryEncodedColumn.getIndex(row.getValue(0));
            ImmutableBitMap bitMap = bitmapIndexedColumn.getBitMapIndex(decreaseIndex);
            allShowIndex = allShowIndex.getAndNot(bitMap);
            segment.putAllShowIndex(allShowIndex);
        }
        release();
        return true;
    }

    public abstract void release();
}
