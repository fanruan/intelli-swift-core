package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Xiaolei.Liu on 2018/1/18
 */
public class SegmentDetailResultSet extends DetailResultSet {


    /**
     * 行号
     */
    private int index = -1;

    /**
     * 列
     */
    private List<Column> columnList;

    /**
     * 明细过滤条件
     */
    private DetailFilter filter;

    public SegmentDetailResultSet(List<Column> columnList, DetailFilter filter) {
        this.columnList = columnList;
        this.filter = filter;
        init();
    }

    @Override
    public void close() throws SQLException {

    }


    @Override
    public boolean next() throws SQLException {
        if (rowCount < maxRow) {
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getRowData() throws SQLException {
        ImmutableBitMap rowIndex = filter.createFilterIndex();
        List values = new ArrayList();
        while (true) {
            index ++;
            if (rowIndex.contains(index)) {
                break;
            }
        }
        for (int i = 0; i < columnList.size(); i++) {
            values.add(columnList.get(i).getDetailColumn().get(index));
        }
        rowCount ++;
        return new ListBasedRow(values);
    }

    public int getMaxRow() {
        return maxRow;
    }

    private void init() {
        this.maxRow = filter.createFilterIndex().getCardinality();
    }

    public int getColumnCount() {
        return columnList.size();
    }
}
