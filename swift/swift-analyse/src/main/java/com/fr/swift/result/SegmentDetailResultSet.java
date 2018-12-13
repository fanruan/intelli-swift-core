package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.IntIterable.IntIterator;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/18
 */
public class SegmentDetailResultSet extends AbstractDetailResultSet {

    /**
     * 当前块中过滤后的行号
     */
    private IntIterator rowItr;

    private int rowCount;

    private List<Column> columnList;

    private Iterator<Row> iterator;

    public SegmentDetailResultSet(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter) {
        super(fetchSize);
        this.columnList = SortSegmentDetailResultSet.getColumnList(columnList);
        ImmutableBitMap filterIndex = filter.createFilterIndex();
        rowCount = filterIndex.getCardinality();
        this.rowItr = filterIndex.intIterator();
    }

    @Override
    public List<Row> getPage() {
        if (!hasNextPage()) {
            return new ArrayList<Row>(0);
        }
        List<Row> page = new ArrayList<Row>();
        int count = fetchSize;
        while (rowItr.hasNext() && count-- > 0) {
            page.add(readRow(rowItr.nextInt(), columnList));
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
        return rowItr.hasNext();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean hasNext() {
        if (iterator == null) {
            iterator = new SwiftRowIteratorImpl(this);
        }
        return iterator.hasNext();
    }

    @Override
    public Row getNextRow() {
        return iterator.next();
    }

    @Override
    public void close() {
        SegmentUtils.releaseColumns(columnList);
    }

}
