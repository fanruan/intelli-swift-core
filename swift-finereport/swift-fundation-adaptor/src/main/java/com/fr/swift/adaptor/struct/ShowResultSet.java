package com.fr.swift.adaptor.struct;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author anchore
 * @date 2018/3/16
 * <p>
 * 用于前端展示的
 * 目前只是把日期类型long包装成了一个java.util.Date
 */
public class ShowResultSet implements SwiftResultSet {
    private SwiftResultSet origin;
    private SwiftMetaData meta;
    /**
     * 日期类型列的列号
     */
    private Set<Integer> dateColumnIndices;

    private ShowResultSet(SwiftResultSet origin, SwiftMetaData meta) throws SQLException {
        this.origin = origin;
        this.meta = meta;
        init();
    }

    /**
     * origin自带meta时用
     *
     * @param origin 源result set
     * @return 展示的result set
     * @throws SQLException 异常
     */
    public static SwiftResultSet of(SwiftResultSet origin) throws SQLException {
        return of(origin, origin.getMetaData());
    }

    /**
     * origin没有meta时或者meta不对时用
     *
     * @param origin 源result set
     * @return 展示的result set
     * @throws SQLException 异常
     */
    public static SwiftResultSet of(SwiftResultSet origin, SwiftMetaData metaIfNeed) throws SQLException {
        return new ShowResultSet(origin, metaIfNeed);
    }

    private void init() throws SQLException {
        if (meta == null) {
            return;
        }
        dateColumnIndices = new HashSet<Integer>();
        for (int i = 0; i < meta.getColumnCount(); i++) {
            if (isDate(meta, i + 1)) {
                dateColumnIndices.add(i);
            }
        }
    }

    private static boolean isDate(SwiftMetaData meta, int i) throws SwiftMetaDataException {
        return ColumnType.DATE == ColumnTypeUtils.getColumnType(meta.getColumn(i));
    }

    @Override
    public void close() throws SQLException {
        origin.close();
    }

    @Override
    public boolean next() throws SQLException {
        return origin != null && origin.next();
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public Row getRowData() throws SQLException {
        return new Row() {
            Row row = origin.getRowData();

            @Override
            public <V> V getValue(int index) {
                Object val = row.getValue(index);
                if (val == null) {
                    return null;
                }
                if (dateColumnIndices != null && dateColumnIndices.contains(index)) {
                    return (V) new java.util.Date((Long) val);
                }
                return (V) val;
            }

            @Override
            public int getSize() {
                return row.getSize();
            }
        };
    }
}