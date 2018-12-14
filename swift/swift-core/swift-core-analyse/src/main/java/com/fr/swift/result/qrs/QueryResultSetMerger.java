package com.fr.swift.result.qrs;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lyon on 2018/11/21.
 */
public interface QueryResultSetMerger<T extends Serializable, Q extends QueryResultSet<T>> {

    /**
     * 合并多个结果集
     *
     * @param resultSets 多个部分结果集
     * @return 合并结果集（可分页）
     */
    Q merge(List<Q> resultSets);
}
