package com.fr.swift.config.entity;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;
import com.fr.swift.config.ColumnIndexingConf;
import com.fr.swift.config.entity.key.ColumnId;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Assert;


/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_col_idx_conf")
public class SwiftColumnIndexingConf implements ColumnIndexingConf {
    @Id
    private ColumnId columnId;

    @Column(name = "isRequireIndex")
    private boolean requireIndex;

    @Column(name = "isRequireGlobalDict")
    private boolean requireGlobalDict;

    public SwiftColumnIndexingConf() {
    }

    public SwiftColumnIndexingConf(SourceKey tableKey, String columnName, boolean requireIndex, boolean requireGlobalDict) {
        Assert.isFalse(!requireIndex && requireGlobalDict, "global dict is not allowed to generate without index");

        this.columnId = new ColumnId(tableKey, columnName);
        this.requireIndex = requireIndex;
        this.requireGlobalDict = requireGlobalDict;
    }

    @Override
    public SourceKey getTable() {
        return columnId.getTableKey();
    }

    @Override
    public String getColumn() {
        return columnId.getColumnName();
    }

    @Override
    public boolean isRequireIndex() {
        return requireIndex;
    }

    @Override
    public boolean isRequireGlobalDict() {
        return requireGlobalDict;
    }

}