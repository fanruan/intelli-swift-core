package com.fr.swift.conf.business.table2source;

/**
 * @author yee
 * @date 2018/3/23
 */
public interface TableToSource {
    String getTableId();
    String getSourceKey();
    void setTableId(String tableId);
    void setSourceKey(String sourceKey);
}
