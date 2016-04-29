package com.fr.bi.stable.relation;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBasicRelation<T,F> {
    public static final String XML_TAG = "BIBasicRelation";
    private static final long serialVersionUID = -1939057877078469485L;
    protected F primaryField;
    protected F foreignField;
    protected T primaryTable;
    protected T foreignTable;

    public BIBasicRelation() {
    }

    public BIBasicRelation(F primaryField, F foreignField, T primaryTable, T foreignTable) {
        this.primaryField = primaryField;
        this.foreignField = foreignField;
        this.primaryTable = primaryTable;
        this.foreignTable = foreignTable;
        BINonValueUtils.checkNull(this.primaryField, primaryTable, this.foreignField, foreignTable);
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


    /**
     * 克隆
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        final StringBuffer sb = new StringBuffer("");
        sb.append(primaryTable).append("->");
        sb.append(foreignTable);
        sb.append("");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = primaryField != null ? primaryField.hashCode() : 0;
        result = 31 * result + (foreignField != null ? foreignField.hashCode() : 0);
        result = 31 * result + (primaryTable != null ? primaryTable.hashCode() : 0);
        result = 31 * result + (foreignTable != null ? foreignTable.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BITableRelation)) {
            return false;
        }

        BIBasicRelation that = (BIBasicRelation) o;

        if (primaryField != null ? !ComparatorUtils.equals(primaryField, that.primaryField) : that.primaryField != null) {
            return false;
        }
        if (foreignField != null ? !ComparatorUtils.equals(foreignField, that.foreignField) : that.foreignField != null) {
            return false;
        }
        if (primaryTable != null ? !ComparatorUtils.equals(primaryTable, that.primaryTable) : that.primaryTable != null) {
            return false;
        }
        return !(foreignTable != null ? !ComparatorUtils.equals(foreignTable, that.foreignTable) : that.foreignTable != null);
    }
}
