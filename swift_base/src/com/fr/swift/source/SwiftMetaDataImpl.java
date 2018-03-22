package com.fr.swift.source;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.config.pojo.MetaDataColumnPojo;
import com.fr.swift.config.pojo.SwiftMetaDataPojo;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 持久化的Table对象
 *
 * @author Daniel-pc
 * @author Connery
 */
public class SwiftMetaDataImpl implements SwiftMetaData {
    private static final long serialVersionUID = 5516973769561307468L;

    private SwiftMetaDataPojo swiftMetaDataPojo;

    private List<SwiftMetaDataColumn> fieldList;

    public SwiftMetaDataImpl(String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, null, null, fieldList);
    }

    public SwiftMetaDataImpl(String tableName, String tableNameRemark, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, tableNameRemark, null, fieldList);
    }

    public SwiftMetaDataImpl(String tableName, String tableNameRemark, String schema, List<SwiftMetaDataColumn> fieldList) {
        Util.requireNonNull(tableName, fieldList);
        List<MetaDataColumnPojo> fieldPojoList = new ArrayList<MetaDataColumnPojo>();
        for (SwiftMetaDataColumn column : fieldList) {
            fieldPojoList.add(column.getMetaDataColumnPojo());
        }
        swiftMetaDataPojo = new SwiftMetaDataPojo(schema, tableName, tableNameRemark, fieldPojoList);
        this.fieldList = fieldList;
    }

    @Override
    public String getSchemaName() {
        return swiftMetaDataPojo.getSchema();
    }

    /**
     * @return 表名
     */
    @Override
    public String getTableName() {
        return swiftMetaDataPojo.getTableName();
    }

    @Override
    public int getColumnCount() {
        return fieldList.size();
    }

    @Override
    public String getColumnName(int column) throws SwiftMetaDataException {
        return getColumn(column).getName();
    }

    @Override
    public String getColumnRemark(int column) throws SwiftMetaDataException {
        return getColumn(column).getRemark();
    }

    @Override
    public int getColumnType(int column) throws SwiftMetaDataException {
        return getColumn(column).getType();
    }

    @Override
    public int getPrecision(int column) throws SwiftMetaDataException {
        return getColumn(column).getPrecision();
    }

    @Override
    public int getScale(int column) throws SwiftMetaDataException {
        return getColumn(column).getScale();
    }

    @Override
    public String getColumnId(int index) throws SwiftMetaDataException {
        return getColumn(index).getColumnId();
    }

    @Override
    public String getRemark() {
        return swiftMetaDataPojo.getRemark();
    }

    @Override
    public String getColumnId(String columnName) throws SwiftMetaDataException {
        return getColumn(columnName).getColumnId();
    }

    @Override
    public SwiftMetaDataColumn getColumn(int column) throws SwiftMetaDataException {
        if (fieldList == null || fieldList.size() < column) {
            throw new SwiftMetaDataColumnAbsentException(column);
        }
        return fieldList.get(column - 1);
    }

    @Override
    public SwiftMetaDataColumn getColumn(String columnName) throws SwiftMetaDataException {
        if (fieldList == null) {
            throw new SwiftMetaDataColumnAbsentException(columnName);
        }
        for (SwiftMetaDataColumn column : fieldList) {
            String compareName = StringUtils.isNotEmpty(getTableName()) ? getTableName() + column.getName() : column.getName();
            // TODO: 2018/3/21 这边有点乱 
            if (ComparatorUtils.equals(compareName, columnName) || ComparatorUtils.equals(column.getName(), columnName)) {
                return column;
            }
        }
        throw new SwiftMetaDataColumnAbsentException(columnName);
    }

    @Override
    public int getColumnIndex(String columnName) throws SwiftMetaDataException {
        if (fieldList == null) {
            throw new SwiftMetaDataColumnAbsentException(columnName);
        }

        for (int i = 0; i < fieldList.size(); i++) {
            SwiftMetaDataColumn column = fieldList.get(i);
            if (ComparatorUtils.equals(column.getName(), columnName)) {
                return i;
            }
        }
        throw new SwiftMetaDataColumnAbsentException(columnName);
    }

    @Override
    public Iterator<SwiftMetaDataColumn> iterator() {
        return new Iterator<SwiftMetaDataColumn>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < fieldList.size();
            }

            @Override
            public SwiftMetaDataColumn next() {
                return fieldList.get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}