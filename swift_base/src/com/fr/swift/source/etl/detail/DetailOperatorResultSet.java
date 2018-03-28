package com.fr.swift.source.etl.detail;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/1/15.
 */
public class DetailOperatorResultSet implements SwiftResultSet {
    private final SwiftMetaData metaData;
    private final List<ColumnKey[]> fields;
    private final List<Segment[]> segments;
    private List<DictionaryEncodedColumn> columns;
    private int currentRow = -1;
    private int currentTotalRow;
    private int currentSegmentIndex = 0;

    public DetailOperatorResultSet(SwiftMetaData metaData, List<ColumnKey[]> fields, List<Segment[]> segments) {
        Util.requireNonNull(fields, segments);
        this.metaData = metaData;
        this.fields = fields;
        this.segments = segments;
        moveToNextSegment();
    }

    private boolean moveToNextSegment() {
        if (currentSegmentIndex >= getBaseSegments().length) {
            return false;
        }
        columns = new ArrayList<DictionaryEncodedColumn>();
        for (int i = 0; i < fields.size(); i++){
            for (ColumnKey columnKey : fields.get(i)) {
                columns.add(getRelationColumns(columnKey, i));
            }
        }
        currentTotalRow = getBaseSegments()[currentSegmentIndex].getRowCount();
        currentRow = -1;
        currentSegmentIndex++;
        return true;
    }

    private Segment[] getBaseSegments(){
        return segments.get(0);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        while (++currentRow >= currentTotalRow && moveToNextSegment()) {
        }
        return currentRow < currentTotalRow;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() {
        List list = new ArrayList();
        for (DictionaryEncodedColumn column : columns) {
            Object v = null;
            //@yee todo 这边会越界，关联取好了应该就不会越界了
            try {
                v = column.getValue(column.getIndexByRow(currentRow));
            } catch (Exception ignore){
            }
            list.add(v);
        }
        return new ListBasedRow(list);
    }

    //@yee todo 找到关联的column，暂时只取主表的column凑数
    private DictionaryEncodedColumn getRelationColumns(ColumnKey columnKey, int foreignSegIndex) {
        Segment[] foreignSegments = segments.get(foreignSegIndex + 1);
        return foreignSegments[0].getColumn(columnKey).getDictionaryEncodedColumn();
    }
}
