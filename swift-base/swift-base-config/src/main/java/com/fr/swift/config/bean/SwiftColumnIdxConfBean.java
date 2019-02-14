package com.fr.swift.config.bean;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Constructor;

/**
 * @author yee
 * @date 2018-11-26
 */
public class SwiftColumnIdxConfBean implements ObjectConverter {
    public static final Class TYPE = entityType();
    private String tableKey;
    private String columnName;
    private boolean requireIndex;
    private boolean requireGlobalDict;


    public SwiftColumnIdxConfBean(String tableKey, String columnName, boolean requireIndex, boolean requireGlobalDict) {
        this.tableKey = tableKey;
        this.columnName = columnName;
        this.requireIndex = requireIndex;
        this.requireGlobalDict = requireGlobalDict;
    }

    public SwiftColumnIdxConfBean() {
    }

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftColumnIndexingConf");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getTableKey() {
        return tableKey;
    }

    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isRequireIndex() {
        return requireIndex;
    }

    public void setRequireIndex(boolean requireIndex) {
        this.requireIndex = requireIndex;
    }

    public boolean isRequireGlobalDict() {
        return requireGlobalDict;
    }

    public void setRequireGlobalDict(boolean requireGlobalDict) {
        this.requireGlobalDict = requireGlobalDict;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SwiftColumnIdxConfBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
