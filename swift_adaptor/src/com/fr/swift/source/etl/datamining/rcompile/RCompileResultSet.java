package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/30 0030 09:54
 */
public class RCompileResultSet implements SwiftResultSet {

    private String[] columns;
    private int[] columnType;
    private List dataList;
    private SwiftMetaData metaData;
    private int rowCursor, rowCount;
    private TempValue tempValue;

    public RCompileResultSet(String[] columns, int[] columnType, List dataList, SwiftMetaData metaData) {
        this.columns = columns;
        this.columnType = columnType;
        this.dataList = dataList;
        this.metaData = metaData;
        rowCount = ((Object[]) dataList.get(2)).length;
        rowCursor = 0;
        tempValue = new TempValue();
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(rowCursor < rowCount) {
            List list = new ArrayList();
            for(int i = 0; i < columns.length; i ++) {
                int type = columnType[i];
                switch(type) {
                    case Types.DOUBLE: {
                        double v = ((double[]) dataList.get(i + 2))[rowCursor];
                        list.add(v);
                    }
                    case Types.INTEGER: {
                        int v = ((int[]) dataList.get(i + 2))[rowCursor];
                        list.add(v);
                    }
                    default: {
                        String v = ((String[]) dataList.get(i + 2))[rowCursor];
                        list.add(v);
                    }
                }
            }
            tempValue.setRow(new ListBasedRow(list));
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {
        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

        ListBasedRow row;

    }

}
