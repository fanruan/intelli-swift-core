package com.fr.swift.query.column;

import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

import java.util.Map;

/**
 * @author yee
 * @date 2019-07-11
 */
public interface ComplexColumn<T> extends Column<T> {
    Map<ColumnKey, Column<?>> getColumns();

    Column<?> getColumn(ColumnKey columnKey);
}
