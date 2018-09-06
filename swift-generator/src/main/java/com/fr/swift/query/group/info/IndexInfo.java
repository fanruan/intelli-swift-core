package com.fr.swift.query.group.info;

/**
 * Created by Lyon on 2018/7/24.
 */
public interface IndexInfo {

    /**
     * 块索引
     *
     * @return
     */
    boolean isIndexed();

    /**
     * 全局索引
     *
     * @return
     */
    boolean isGlobalIndexed();
}
