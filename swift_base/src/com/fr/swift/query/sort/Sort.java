package com.fr.swift.query.sort;

import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2017/12/11
 */
public interface Sort {
    /**
     * 类型
     *
     * @return
     */
    SortType getSortType();

    /**
     * 依据字段
     *
     * @return
     */
    int getTargetIndex();

    /**
     * 依据字段ColumnKey
     *
     * @return
     */
    ColumnKey getColumnKey();
}
