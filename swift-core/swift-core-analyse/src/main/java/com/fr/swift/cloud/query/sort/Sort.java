package com.fr.swift.cloud.query.sort;

import com.fr.swift.cloud.segment.column.ColumnKey;

import java.io.Serializable;

/**
 * @author pony
 * @date 2017/12/11
 */
public interface Sort extends Serializable {
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
