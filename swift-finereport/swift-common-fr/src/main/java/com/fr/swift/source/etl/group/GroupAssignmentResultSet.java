package com.fr.swift.source.etl.group;

import com.fr.stable.StringUtils;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Handsome on 2018/2/22 0022 11:54
 */
public class GroupAssignmentResultSet implements SwiftResultSet {

    //为空时表示未分组用原始名字
    private String otherName;
    private ColumnKey columnKey;
    private List<SingleGroup> group;
    private SwiftMetaData metaData;
    private Segment[] segments;
    private Map<Object, String> resMap;
    private int segCursor, rowCursor, rowCount;
    private TempValue tempValue;

    public GroupAssignmentResultSet(String otherName, ColumnKey columnKey, List<SingleGroup> group,
                                    SwiftMetaData metaData, Segment[] segments) {
        this.otherName = otherName;
        this.columnKey = columnKey;
        this.group = group;
        this.metaData = metaData;
        this.segments = segments;
        init();
    }

    private void init() {
        tempValue = new TempValue();
        resMap = new HashMap<Object, String>();
        Iterator<SingleGroup> iterator = group.iterator();
        while (iterator.hasNext()) {
            SingleGroup singleGroup = iterator.next();
            String name = singleGroup.getName();
            List<String> list = singleGroup.getList();
            Iterator<String> valueIter = list.iterator();
            while (valueIter.hasNext()) {
                String value = valueIter.next();
                resMap.put(value, name);
            }
        }
        segCursor = 0;
        rowCursor = 0;
        rowCount = segments[0].getRowCount();
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Object ob = getter.getValue(getter.getIndexByRow(rowCursor));
            Object value = resMap.get(ob);
            if (value == null) {
                value = StringUtils.isEmpty(otherName) ? ob : otherName;
            }
            List<Object> dataList = new ArrayList<Object>();
            dataList.add(value);
            tempValue.setRow(new ListBasedRow(dataList));
            if (rowCursor < rowCount - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    segCursor++;
                    rowCursor = 0;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {
        ListBasedRow row;

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

    }
}
