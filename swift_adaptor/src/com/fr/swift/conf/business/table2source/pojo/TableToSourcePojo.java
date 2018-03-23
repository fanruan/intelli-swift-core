package com.fr.swift.conf.business.table2source.pojo;

import com.fr.swift.conf.business.table2source.TableToSource;

/**
 * @author yee
 * @date 2018/3/23
 */
public class TableToSourcePojo implements TableToSource {

    private String tableId;
    private String sourceKey;

    public TableToSourcePojo() {
    }

    public TableToSourcePojo(String tableId, String sourceKey) {
        this.tableId = tableId;
        this.sourceKey = sourceKey;
    }

    @Override
    public String getTableId() {
        return tableId;
    }

    @Override
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @Override
    public String getSourceKey() {
        return sourceKey;
    }

    @Override
    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableToSourcePojo that = (TableToSourcePojo) o;

        if (tableId != null ? !tableId.equals(that.tableId) : that.tableId != null) return false;
        return sourceKey != null ? sourceKey.equals(that.sourceKey) : that.sourceKey == null;
    }

    @Override
    public int hashCode() {
        int result = tableId != null ? tableId.hashCode() : 0;
        result = 31 * result + (sourceKey != null ? sourceKey.hashCode() : 0);
        return result;
    }
}
