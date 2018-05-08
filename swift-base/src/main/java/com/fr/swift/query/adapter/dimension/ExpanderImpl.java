package com.fr.swift.query.adapter.dimension;

import com.fr.swift.result.row.RowIndexKey;

import java.util.Set;

/**
 * 节点展开信息，数据结构为行结构
 */
public class ExpanderImpl implements Expander {

    private int nLevel;
    private ExpanderType type;
    private Set<RowIndexKey<String[]>> strIndexKeys;

    public ExpanderImpl(ExpanderType type, Set<RowIndexKey<String[]>> strIndexKeys) {
        this.type = type;
        this.strIndexKeys = strIndexKeys;
    }

    public ExpanderImpl(int nLevel, ExpanderType type, Set<RowIndexKey<String[]>> strIndexKeys) {
        this.nLevel = nLevel;
        this.type = type;
        this.strIndexKeys = strIndexKeys;
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
    @Override
    public Set<RowIndexKey<String[]>> getStringIndexKeys() {
        return strIndexKeys;
    }
}