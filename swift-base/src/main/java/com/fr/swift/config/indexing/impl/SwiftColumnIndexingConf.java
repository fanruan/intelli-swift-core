package com.fr.swift.config.indexing.impl;

import com.fr.swift.config.indexing.ColumnIndexingConf;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_column_index_conf")
public class SwiftColumnIndexingConf implements ColumnIndexingConf {
    @Id
    private ColumnId columnId;

    @Column(name = "requireIndex")
    private boolean requireIndex;

    @Column(name = "requireGlobalDict")
    private boolean requireGlobalDict;

    @Override
    public SourceKey getTable() {
        return columnId.getTableKey();
    }

    @Override
    public String getColumn() {
        return columnId.getColumnName();
    }

    @Override
    public boolean requireIndex() {
        return requireIndex;
    }

    @Override
    public boolean requireGlobalDict() {
        return requireGlobalDict;
    }

    public SwiftColumnIndexingConf() {
    }

    public SwiftColumnIndexingConf(SourceKey tableKey, String columnName, boolean requireIndex, boolean requireGlobalDict) {
        Crasher.crashIf(!requireIndex && requireGlobalDict, "global dict is not allowed to generate with no index");

        this.columnId = new ColumnId(tableKey, columnName);
        this.requireIndex = requireIndex;
        this.requireGlobalDict = requireGlobalDict;
    }
}