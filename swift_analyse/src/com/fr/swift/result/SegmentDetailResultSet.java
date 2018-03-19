package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Xiaolei.Liu on 2018/1/18
 */
public class SegmentDetailResultSet extends DetailResultSet {
    /**
     * 行号
     */
    private int row = -1;

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
    public Row getRowData() {
        ImmutableBitMap rowIndex = filter.createFilterIndex();
        List values = new ArrayList();
        while (true) {
            row++;
            if (rowIndex.contains(row)) {
                break;
            }
        }
        for (int i = 0; i < columnList.size(); i++) {
            DictionaryEncodedColumn column = columnList.get(i).getDictionaryEncodedColumn();
            Object val = column.getValue(column.getIndexByRow(row));
            values.add(val);
        }
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
