package com.fr.swift.cloud.result.qrs;

import java.util.List;

/**
 * This class created on 2018/11/21
 *
 * @author lyon
 * @description
 */
public interface QueryResultSetMerger<Q extends QueryResultSet<?>> {

    /**
     * 合并多个结果集
     *
     * @param resultSets 多个部分结果集
     * @return 合并结果集（可分页）
     */
    Q merge(List<Q> resultSets);
}
