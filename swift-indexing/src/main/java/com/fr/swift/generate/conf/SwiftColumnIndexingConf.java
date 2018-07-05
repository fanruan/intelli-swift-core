package com.fr.swift.generate.conf;

import com.fr.swift.source.SourceKey;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

import java.io.Serializable;

/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_column_index_conf")
public class SwiftColumnIndexingConf extends BaseIndexingConf implements ColumnIndexingConf, Serializable {
    @Id
    @Column(name = "columnName")
    private String column;

    @Column(name = "requireIndex")
    private boolean requireIndex;

    @Column(name = "requireGlobalDict")
    private boolean requireGlobalDict;

    public SwiftColumnIndexingConf(SourceKey table, String column, boolean requireIndex, boolean requireGlobalDict) {
        super(table);
        this.column = column;
        this.requireIndex = requireIndex;
        this.requireGlobalDict = requireGlobalDict;
    }

    @Override
    public SourceKey getTable() {
        return new SourceKey(tableKey);
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public boolean requireIndex() {
        return requireIndex;
    }

    @Override
    public boolean requireGlobalDict() {
        return requireGlobalDict;
    }
}