package com.fr.swift.result;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/18
 */
public class SegmentDetailResultSet implements DetailResultSet {

    // 明细行的游标
    private int rowCursor = 0;
    // 当前块中过滤后的行号
    private IntArray rows;
    private List<Column> columnList;
    private SwiftMetaData metaData;
    private Iterator<Row> iterator;

    public SegmentDetailResultSet(List<Column> columnList, DetailFilter filter, SwiftMetaData metaData) {
        this.columnList = columnList;
        this.rows = BitMaps.traversal2Array(filter.createFilterIndex());
        this.metaData = metaData;
    }

    @Override
    public List<Row> getPage() {
        if (!hasNextPage()) {
            return new ArrayList<Row>(0);
        }
        List<Row> page = new ArrayList<Row>();
        int count = PAGE_SIZE;
        while (rowCursor < rows.size() && count-- > 0) {
            page.add(readRow(rows.get(rowCursor), columnList));
            rowCursor++;
        }
        return page;
    }

    static Row readRow(int row, List<Column> columnList) {
        List<Object> values = new ArrayList<Object>();
        for (Column column : columnList) {
            DictionaryEncodedColumn dictionary = column.getDictionaryEncodedColumn();
            Object val = dictionary.getValueByRow(row);
            values.add(val);
        }
        return new ListBasedRow(values);
    }

    @Override
    public boolean hasNextPage() {
        return rowCursor < rows.size();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean next() {
        if (iterator == null) {
            iterator = new SwiftRowIteratorImpl(this);
        }
        return iterator.hasNext();
    }

    @Override
    public Row getRowData() {
        return iterator.next();
    }

    @Override
    public void close() {

    }

}
