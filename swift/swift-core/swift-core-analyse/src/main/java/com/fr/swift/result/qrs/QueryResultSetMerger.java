package com.fr.swift.result.qrs;

import java.util.List;

/**
 *
 * @author lyon
 * @date 2018/11/21
 */
public interface QueryResultSetMerger<T> {

    /**
     * 合并多个结果集
     *
     * @param resultSets 多个部分结果集
     * @return 合并结果集（可分页）
     */
    QueryResultSet<T> merge(List<QueryResultSet<T>> resultSets);
}
