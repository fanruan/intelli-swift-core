package com.fr.swift.segment.relation.impl;

import com.fr.swift.segment.relation.CubeMultiRelation;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Util;

/**
 * @author yee
 * @date 2018/1/17
 */
public class CubeMultiRelationImpl implements CubeMultiRelation<CubeLogicColumnKey> {

    protected CubeLogicColumnKey primaryField;
    protected CubeLogicColumnKey foreignField;
    protected SourceKey primaryTable;
    protected SourceKey foreignTable;

    public CubeMultiRelationImpl(CubeLogicColumnKey primaryField, CubeLogicColumnKey foreignField, SourceKey primaryTable, SourceKey foreignTable) {
        Util.requireNonNull(primaryField, foreignField, primaryTable, foreignTable);
        this.primaryField = primaryField;
        this.foreignField = foreignField;
        this.primaryTable = primaryTable;
        this.foreignTable = foreignTable;
    }

    @Override
    public String getKey() {
        return MD5Utils.getMD5String(new String[]{primaryField.getFieldName(), foreignField.getFieldName()});
    }

    @Override
    public CubeLogicColumnKey getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(CubeLogicColumnKey primaryField) {
        this.primaryField = primaryField;
    }

    @Override
    public CubeLogicColumnKey getForeignField() {
        return foreignField;
    }

    public void setForeignField(CubeLogicColumnKey foreignField) {
        this.foreignField = foreignField;
    }

    @Override
    public SourceKey getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(SourceKey primaryTable) {
        this.primaryTable = primaryTable;
    }

    @Override
    public SourceKey getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(SourceKey foreignTable) {
        this.foreignTable = foreignTable;
    }

    @Override
    public CubeLogicColumnKey getPrimaryKey() {
        return getPrimaryField();
    }

    @Override
    public CubeLogicColumnKey getForeignKey() {
        return getForeignField();
    }


    /**
     * 是否是自己连的自己
     *
     * @return 是否是自己连的自己
     */
    public boolean isSelfRelation() {
        return Util.equals(primaryTable, foreignTable) && Util.equals(primaryField, foreignField);
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
        CubeMultiRelationImpl cloned = (CubeMultiRelationImpl) super.clone();
        return cloned;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof CubeMultiRelationImpl
                && Util.equals(primaryField, ((CubeMultiRelationImpl) o).primaryField)
                && Util.equals(foreignField, ((CubeMultiRelationImpl) o).foreignField)
                && Util.equals(primaryTable, ((CubeMultiRelationImpl) o).primaryTable)
                && Util.equals(foreignTable, ((CubeMultiRelationImpl) o).foreignTable);
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
