package com.fr.swift.query.result;

import com.fr.swift.query.LocalQuery;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * 对查询结果的合并
 * @param <T>
 */
public interface ResultQuery<T extends QueryResultSet> extends LocalQuery<T> {
}
