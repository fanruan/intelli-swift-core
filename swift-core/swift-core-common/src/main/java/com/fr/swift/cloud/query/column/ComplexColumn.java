package com.fr.swift.cloud.query.column;

import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;

import java.util.Map;

/**
 * 多字段聚合的复杂column
 *
 * @author yee
 * @date 2019-07-11
 */
public interface ComplexColumn<T> extends Column<T> {
    /**
     * 获取所有用于组合的column
     *
     * @return
     */
    Map<ColumnKey, Column<?>> getColumns();

    /**
     * 根据columnKey获取
     * @param columnKey
     * @return
     */
    Column<?> getColumn(ColumnKey columnKey);
}
