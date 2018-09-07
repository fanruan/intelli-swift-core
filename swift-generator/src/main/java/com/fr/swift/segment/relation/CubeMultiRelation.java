package com.fr.swift.segment.relation;

import com.fr.general.ComparatorUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Util;

/**
 * @author yee
 * @date 2018/1/17
 */
public class CubeMultiRelation {

    protected CubeLogicColumnKey primaryField;
    protected CubeLogicColumnKey foreignField;
    protected SourceKey primaryTable;
    protected SourceKey foreignTable;

    public CubeMultiRelation(CubeLogicColumnKey primaryField, CubeLogicColumnKey foreignField, SourceKey primaryTable, SourceKey foreignTable) {
        Util.requireNonNull(primaryField, foreignField, primaryTable, foreignTable);
        this.primaryField = primaryField;
        this.foreignField = foreignField;
        this.primaryTable = primaryTable;
        this.foreignTable = foreignTable;
    }

    public String getKey() {
        return MD5Utils.getMD5String(new String[]{primaryField.getFieldName(), foreignField.getFieldName()});
    }

    public CubeLogicColumnKey getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(CubeLogicColumnKey primaryField) {
        this.primaryField = primaryField;
    }

    public CubeLogicColumnKey getForeignField() {
        return foreignField;
    }

    public void setForeignField(CubeLogicColumnKey foreignField) {
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

    public CubeLogicColumnKey getPrimaryKey() {
        return getPrimaryField();
    }

    public CubeLogicColumnKey getForeignKey() {
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
        CubeMultiRelation cloned = (CubeMultiRelation) super.clone();
        return cloned;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof CubeMultiRelation
                && ComparatorUtils.equals(primaryField, ((CubeMultiRelation) o).primaryField)
                && ComparatorUtils.equals(foreignField, ((CubeMultiRelation) o).foreignField)
                && ComparatorUtils.equals(primaryTable, ((CubeMultiRelation) o).primaryTable)
                && ComparatorUtils.equals(foreignTable, ((CubeMultiRelation) o).foreignTable);
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
