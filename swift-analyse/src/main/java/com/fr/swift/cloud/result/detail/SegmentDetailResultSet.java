package com.fr.swift.cloud.result.detail;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.result.BaseDetailQueryResultSet;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.source.ListBasedRow;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.structure.IntIterable;
import com.fr.swift.cloud.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/18
 *
 * @author yee
 */
public class SegmentDetailResultSet extends BaseDetailQueryResultSet {

    /**
     * 当前块中过滤后的行号
     */
    private IntIterable.IntIterator rowItr;

    private int rowCount;

    private List<Column<?>> columnList;


    public SegmentDetailResultSet(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter, Limit limit) {
        super(fetchSize);
        this.columnList = SortSegmentDetailResultSet.getColumnList(columnList);
        ImmutableBitMap filterIndex = filter.createFilterIndex();
        rowCount = filterIndex.getCardinality();
        if (limit != null) {
            rowCount = rowCount > limit.end() ? limit.end() : rowCount;
        }
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

    static Row readRow(int row, List<Column<?>> columnList) {
        List<Object> values = new ArrayList<Object>();
        for (Column<?> column : columnList) {
            DetailColumn<?> detailColumn = column.getDetailColumn();
            Object val = detailColumn.get(row);
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
    public void close() {
        SegmentUtils.releaseHisColumn(columnList);
    }

}
