package com.fr.swift.adaptor.struct;

import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * This class created on 2018-1-29 10:35:58
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftEmptyResult implements SwiftResultSet{


    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return new SwiftMetaData() {
            @Override
            public String getTableName() {
                return null;
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public String getColumnName(int column) {
                return null;
            }

            @Override
            public String getColumnRemark(int column) {
                return null;
            }

            @Override
            public Schema getSwiftSchema() {
                return null;
            }

            @Override
            public String getSchemaName() {
                return null;
            }

            @Override
            public int getScale(int column) {
                return 0;
            }

            @Override
            public int getColumnType(int column) {
                return 0;
            }

            @Override
            public int getPrecision(int column) {
                return 0;
            }

            @Override
            public SwiftMetaDataColumn getColumn(int column) {
                return null;
            }

            @Override
            public SwiftMetaDataColumn getColumn(String columnName) {
                return null;
            }

            @Override
            public int getColumnIndex(String columnName) {
                return 0;
            }

            @Override
            public String getColumnId(int index) {
                return null;
            }

            @Override
            public String getRemark() {
                return null;
            }

            @Override
            public String getColumnId(String columnName) {
                return null;
            }

            @Override
            public List<String> getFieldNames() {
                return null;
            }
        };
    }

    @Override
    public Row getRowData() {
        return null;
    }


}
