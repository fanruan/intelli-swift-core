package com.finebi.cube.relation;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.io.Serializable;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBasicRelation<T, F> implements Serializable {
    public static final String XML_TAG = "BIBasicRelation";
    private static final long serialVersionUID = -1939057877078469485L;
    protected F primaryField;
    protected F foreignField;
    protected T primaryTable;
    protected T foreignTable;

    public BIBasicRelation() {
    }

    public BIBasicRelation(F primaryField, F foreignField, T primaryTable, T foreignTable) {
        BINonValueUtils.checkNull(primaryField, primaryTable, foreignField, foreignTable);
        this.primaryField = primaryField;
        this.foreignField = foreignField;
        this.primaryTable = primaryTable;
        this.foreignTable = foreignTable;
    }


    public F getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(F primaryField) {
        this.primaryField = primaryField;
    }

    public F getForeignField() {
        return foreignField;
    }

    public void setForeignField(F foreignField) {
        this.foreignField = foreignField;
    }

    public T getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(T primaryTable) {
        this.primaryTable = primaryTable;
    }

    public T getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(T foreignTable) {
        this.foreignTable = foreignTable;
    }


    public F getPrimaryKey() {
        return getPrimaryField();
    }

    public F getForeignKey() {
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
        BIBasicRelation cloned = (BIBasicRelation) super.clone();

        return cloned;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof BIBasicRelation
                && ComparatorUtils.equals(primaryField, ((BIBasicRelation) o).primaryField)
                && ComparatorUtils.equals(foreignField, ((BIBasicRelation) o).foreignField)
                && ComparatorUtils.equals(primaryTable, ((BIBasicRelation) o).primaryTable)
                && ComparatorUtils.equals(foreignTable, ((BIBasicRelation) o).foreignTable);
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
