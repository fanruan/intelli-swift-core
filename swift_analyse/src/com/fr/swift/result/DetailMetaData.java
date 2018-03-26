package com.fr.swift.result;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.Iterator;

public class DetailMetaData implements SwiftMetaData {
    @Override
    public String getSchemaName() throws SwiftMetaDataException {
        return null;
    }

    @Override
    public String getTableName() throws SwiftMetaDataException {
        return null;
    }

    @Override
    public int getColumnCount() throws SwiftMetaDataException {
        return 0;
    }

    @Override
    public String getColumnName(int index) throws SwiftMetaDataException {
        return null;
    }

    @Override
    public String getColumnRemark(int index) throws SwiftMetaDataException {
        return null;
    }

    @Override
    public int getColumnType(int index) throws SwiftMetaDataException {
        return 0;
    }

    @Override
    public int getPrecision(int index) throws SwiftMetaDataException {
        return 0;
    }

    @Override
    public int getScale(int index) throws SwiftMetaDataException {
        return 0;
    }

    @Override
    public SwiftMetaDataColumn getColumn(int index) throws SwiftMetaDataException {
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
    public String getColumnId(int index) throws SwiftMetaDataException {
        return null;
    }

    @Override
    public String getColumnId(String columnName) throws SwiftMetaDataException {
        return null;
    }

    @Override
    public String getRemark() throws SwiftMetaDataException {
        return null;
    }

    @Override
    public Iterator<SwiftMetaDataColumn> iterator() {
        return null;
    }
}
