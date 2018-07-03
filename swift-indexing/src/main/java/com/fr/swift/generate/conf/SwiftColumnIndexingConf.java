package com.fr.swift.generate.conf;

import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/7/2
 */
public class SwiftColumnIndexingConf extends BaseIndexingConf implements ColumnIndexingConf {
    private String columnName;

    private boolean requireIndex;

    private boolean requireGlobalDict;

    public SwiftColumnIndexingConf(ColumnIndexingConf conf) {
        this(conf.getTable(), conf.getColumn(), conf.requireIndex(), conf.requireGlobalDict());
    }

    public SwiftColumnIndexingConf(SourceKey table, String columnName, boolean requireIndex, boolean requireGlobalDict) {
        super(table);
        this.columnName = columnName;
        this.requireIndex = requireIndex;
        this.requireGlobalDict = requireGlobalDict;
    }

    @Override
    public SourceKey getTable() {
        return table;
    }

    @Override
    public String getColumn() {
        return columnName;
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