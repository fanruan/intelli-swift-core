package com.fr.swift.relation;

import com.fr.general.ComparatorUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Util;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BICubeMultiRelation {

    protected BICubeLogicColumnKey primaryField;
    protected BICubeLogicColumnKey foreignField;
    protected SourceKey primaryTable;
    protected SourceKey foreignTable;

    public BICubeMultiRelation(BICubeLogicColumnKey primaryField, BICubeLogicColumnKey foreignField, SourceKey primaryTable, SourceKey foreignTable) {
        Util.requireNonNull(primaryField, foreignField, primaryTable, foreignTable);
        this.primaryField = primaryField;
        this.foreignField = foreignField;
        this.primaryTable = primaryTable;
        this.foreignTable = foreignTable;
    }

    public String getKey() {
        return MD5Utils.getMD5String(new String[]{primaryField.getKey(), foreignField.getKey()});
    }

    public BICubeLogicColumnKey getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(BICubeLogicColumnKey primaryField) {
        this.primaryField = primaryField;
    }

    public BICubeLogicColumnKey getForeignField() {
        return foreignField;
    }

    public void setForeignField(BICubeLogicColumnKey foreignField) {
        this.foreignField = foreignField;
    }

    public SourceKey getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(SourceKey primaryTable) {
        this.primaryTable = primaryTable;
    }

    public SourceKey getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(SourceKey foreignTable) {
        this.foreignTable = foreignTable;
    }

    public BICubeLogicColumnKey getPrimaryKey() {
        return getPrimaryField();
    }

    public BICubeLogicColumnKey getForeignKey() {
        return getForeignField();
    }


    /**
     * 是否是自己连的自己
     *
     * @return 是否是自己连的自己
     */
    public boolean isSelfRelation() {
        return ComparatorUtils.equals(primaryTable, foreignTable) && ComparatorUtils.equals(primaryField, foreignField);
    }

    @Override
    public String toString() {
        return primaryTable + "->" + foreignTable;
    }

    /**
     * 克隆
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BICubeMultiRelation cloned = (BICubeMultiRelation) super.clone();
        return cloned;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof BICubeMultiRelation
                && ComparatorUtils.equals(primaryField, ((BICubeMultiRelation) o).primaryField)
                && ComparatorUtils.equals(foreignField, ((BICubeMultiRelation) o).foreignField)
                && ComparatorUtils.equals(primaryTable, ((BICubeMultiRelation) o).primaryTable)
                && ComparatorUtils.equals(foreignTable, ((BICubeMultiRelation) o).foreignTable);
    }

    @Override
    public int hashCode() {
        int result = primaryField != null ? primaryField.hashCode() : 0;
        result = 31 * result + (foreignField != null ? foreignField.hashCode() : 0);
        result = 31 * result + (primaryTable != null ? primaryTable.hashCode() : 0);
        result = 31 * result + (foreignTable != null ? foreignTable.hashCode() : 0);
        return result;
    }
}
