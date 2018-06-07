package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Xiaolei.Liu on 2018/1/18
 */
public class SegmentDetailResultSet extends DetailResultSet {

    // resultSet迭代器的游标
    private int row = -1;
    // 字段列
    private List<Column> columnList;
    // 过滤结果(所有行)的bitmap
    private ImmutableBitMap bitMapOfTotalRows;
    private SwiftMetaData metaData;

    public SegmentDetailResultSet(List<Column> columnList, DetailFilter filter, SwiftMetaData metaData) {
        this.columnList = columnList;
        this.bitMapOfTotalRows = filter.createFilterIndex();
        this.metaData = metaData;
        this.maxRow = bitMapOfTotalRows.getCardinality();
    }

    @Override
    public Row getRowData() {
        List values = new ArrayList();
        while (true) {
            row++;
            if (bitMapOfTotalRows.contains(row)) {
                break;
            }
        }
        for (int i = 0; i < columnList.size(); i++) {
            DictionaryEncodedColumn column = columnList.get(i).getDictionaryEncodedColumn();
            Object val = column.getValueByRow(row);
            values.add(val);
        }
        return new ListBasedRow(values);
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }
}
