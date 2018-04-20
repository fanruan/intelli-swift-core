package com.fr.swift.query.adapter.dimension;

import com.fr.swift.result.RowIndexKey;

import java.util.Set;

/**
 * 节点展开信息，数据结构为行结构
 *
 */
public class Expander {

    private int nLevel;
    private ExpanderType type;
    private Set<RowIndexKey<String>> indexKeys;

    public Expander(ExpanderType type, Set<RowIndexKey<String>> indexKeys) {
        this.type = type;
        this.indexKeys = indexKeys;
    }

    public Expander(int nLevel, ExpanderType type, Set<RowIndexKey<String>> indexKeys) {
        this.nLevel = nLevel;
        this.type = type;
        this.indexKeys = indexKeys;
    }

    /**
     * 全部展开到第N个维度
     *
     * @return
     */
    public int getNLevel() {
        return nLevel;
    }

    /**
     * 展开类型
     *
     * @return
     */
    public ExpanderType getType() {
        return type;
    }

    /**
     * 展开（或者不展开）的行索引，对应要进行展开操作的节点（非叶子节点）
     *
     * @return
     */
    public Set<RowIndexKey<String>> getIndexKeys() {
        return indexKeys;
    }
}