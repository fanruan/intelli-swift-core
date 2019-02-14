package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants.COLUMN;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

/**
 * @author anchore
 * @date 2018/2/9
 */
public class ColumnTypeAdaptor {
    public static ColumnType adaptColumnType(int columnType) {
        switch (columnType) {
            case COLUMN.NUMBER:
                return ColumnType.NUMBER;
            case COLUMN.DATE:
                return ColumnType.DATE;
            case COLUMN.STRING:
                return ColumnType.STRING;
            default:
                return ColumnType.STRING;
        }
    }
}