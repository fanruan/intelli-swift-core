package com.fr.swift.adaptor.struct;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * This class created on 2018-1-29 10:35:58
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftEmptyResult implements SwiftResultSet {
    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return new SwiftMetaData() {
            @Override
            public String getTableName() throws SwiftMetaDataException {
                return null;
            }

            @Override
            public String getTableName(int column) throws SwiftMetaDataException {
                return null;
            }

            @Override
            public int getColumnCount() throws SwiftMetaDataException {
                return 0;
            }

            @Override
            public String getColumnName(int column) throws SwiftMetaDataException {
                return null;
            }

            @Override
            public String getColumnRemark(int column) throws SwiftMetaDataException {
                return null;
            }

            @Override
            public String getSchemaName() throws SwiftMetaDataException {
                return null;
            }

            @Override
            public String getSchemaName(int column) throws SwiftMetaDataException {
                return null;
            }

            @Override
            public int getScale(int column) throws SwiftMetaDataException {
                return 0;
            }

            @Override
            public int getColumnType(int column) throws SwiftMetaDataException {
                return 0;
            }

            @Override
            public int getPrecision(int column) throws SwiftMetaDataException {
                return 0;
            }

            @Override
            public SwiftMetaDataColumn getColumn(int column) throws SwiftMetaDataException {
                return null;
            }

            @Override
            public SwiftMetaDataColumn getColumn(String columnName) throws SwiftMetaDataException {
                return null;
            }

            @Override
            public int getColumnIndex(String columnName) throws SwiftMetaDataException {
                return 0;
            }

            @Override
            public Iterator<SwiftMetaDataColumn> iterator() {
                return null;
            }

            @Override
            public void forEach(Consumer<? super SwiftMetaDataColumn> action) {

            }

            @Override
            public Spliterator<SwiftMetaDataColumn> spliterator() {
                return null;
            }
        };
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }
}
