package com.fr.swift.source.db;

import com.fr.general.DateUtils;
import com.fr.stable.Primitive;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pony on 2018/1/17.
 */
public abstract class AbstractDataModelResultSet implements SwiftResultSet {
    protected SwiftMetaData metaData;
    protected ColumnType[] columnTypes;
    protected int[] columnIndexes;
    public AbstractDataModelResultSet(SwiftMetaData metaData, SwiftMetaData outerMetadata) {
        this.metaData = metaData;
        checkColumnsByMetaData(metaData, outerMetadata);
    }

    private void checkColumnsByMetaData(SwiftMetaData metaData, SwiftMetaData outerMetadata) {
        List<Integer> indexList = new ArrayList<Integer>();
        List<ColumnType> typeList = new ArrayList<ColumnType>();
        int columnCount = 0;
        try {
            columnCount = outerMetadata.getColumnCount();
        } catch (SwiftMetaDataException e) {
            //do nothing;
        }
        for (int i = 0; i < columnCount; i++){
            SwiftMetaDataColumn column = null;
            try {
                column = metaData.getColumn(outerMetadata.getColumnName(i+1));
                if (column != null){
                    indexList.add(i);
                    typeList.add(ColumnTypeUtils.getColumnType(column));
                }
            } catch (SwiftMetaDataException e) {
                //do nothing;
            }
        }
        columnIndexes = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++){
            columnIndexes[i] = indexList.get(i);
        }
        columnTypes = new ColumnType[typeList.size()];
        for (int i = 0; i < typeList.size(); i++){
            columnTypes[i] = typeList.get(i);
        }
    }

    protected Object getValue(Object value, ColumnType columnType) {
        if (value == null || value == Primitive.NULL) {
            return null;
        }
        switch (columnType) {
            case NUMBER:
                return dealWithNumber(value);
            case DATE:
                return dealWithDate(value);
            default:
                return value.toString();
        }

    }

    private Object dealWithNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Date) {
            return ((Date) value).getTime();
        } else {
            String str = value.toString();
            //toString之后比较快点吧
            if (Strings.isEmpty(str)) {
                return false;
            }
            try {
                return Double.valueOf(str);
            } catch (Exception ignore) {
                return null;
            }
        }
    }


    private Object dealWithDate(Object value) {
        if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        Date date = DateUtils.string2Date(value.toString(), true);
        return date == null ? null : date.getTime();
    }
}
