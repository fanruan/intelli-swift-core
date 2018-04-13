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
        rowCount = getArrayLength(dataList.get(3));
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
                        break;
                    }
                    case Types.INTEGER: {
                        Long v = ((Long[]) dataList.get(i + 2))[rowCursor];
                        //Long value = Long.parseLong(v + "");
                        list.add(v);
                        break;
                    }
                    case Types.BIT: {
                        byte v = ((byte[]) dataList.get(i + 2))[rowCursor];
                        list.add(v);
                        break;
                    }
                    default: {
                        String v = ((String[]) dataList.get(i + 2))[rowCursor];
                        list.add(v);
                    }
                }
            }
            rowCursor ++;
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

    private int getArrayLength(Object object) {
        if(object instanceof int[]) {
            return ((int[]) object).length;
        } else if(object instanceof double[]) {
            return ((double[]) object).length;
        } else if(object instanceof byte[]) {
            return ((byte[]) object).length;
        } else {
            return ((String[]) object).length;
        }
    }

}
