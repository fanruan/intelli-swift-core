package com.fr.swift.query.group.info.cursor;

import com.fr.swift.result.row.RowIndexKey;

import java.util.Set;

/**
 * Created by Lyon on 2018/4/27.
 */
public interface Expander {

    /**
     * 全部展开到第N个维度
     *
     */
    int getNLevel();

    /**
     * 展开类型
     *
     */
    ExpanderType getType();

    /**
     * 展开（或者不展开）的行索引，对应要进行展开操作的节点（非叶子节点）
     *
     */
    Set<RowIndexKey<String[]>> getStringIndexKeys();
}
