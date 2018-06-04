package com.fr.swift.query.group.info.cursor;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpanderImpl expander = (ExpanderImpl) o;

        if (nLevel != expander.nLevel) return false;
        if (type != expander.type) return false;
        return strIndexKeys != null ? strIndexKeys.equals(expander.strIndexKeys) : expander.strIndexKeys == null;
    }

    @Override
    public int hashCode() {
        int result = nLevel;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (strIndexKeys != null ? strIndexKeys.hashCode() : 0);
        return result;
    }
}