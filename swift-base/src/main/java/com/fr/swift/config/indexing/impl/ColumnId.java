package com.fr.swift.config.indexing.impl;

import com.fr.swift.source.SourceKey;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Embeddable;

/**
 * @author anchore
 * @date 2018/7/17
 */
@Embeddable
class ColumnId extends TableId {
    @Column(name = "columnName")
    private String columnName;

    ColumnId(SourceKey tableKey, String columnName) {
        super(tableKey);
        this.columnName = columnName;
    }

    public ColumnId() {
    }

    public String getColumnName() {
        return columnName;
    }

    private static final long serialVersionUID = 3086657956586601951L;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ColumnId that = (ColumnId) o;

        return columnName != null ? columnName.equals(that.columnName) : that.columnName == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }
}