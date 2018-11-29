package com.fr.swift.config.entity.key;

import com.fr.swift.source.SourceKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author anchore
 * @date 2018/7/17
 */
@Embeddable
@MappedSuperclass
public class TableId implements Serializable {
    private static final long serialVersionUID = -1318234923825121266L;
    @Column(name = "tableKey")
    public String tableKey;

    public TableId(SourceKey tableKey) {
        this.tableKey = tableKey.getId();
    }

    public TableId() {
    }

    public SourceKey getTableKey() {
        return new SourceKey(tableKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableId that = (TableId) o;

        return tableKey != null ? tableKey.equals(that.tableKey) : that.tableKey == null;
    }

    @Override
    public int hashCode() {
        return tableKey != null ? tableKey.hashCode() : 0;
    }
}