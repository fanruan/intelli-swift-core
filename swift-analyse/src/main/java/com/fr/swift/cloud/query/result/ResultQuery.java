package com.fr.swift.cloud.query.result;

import com.fr.swift.cloud.query.LocalQuery;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

/**
 * 对查询结果的合并
 * @param <T>
 */
public interface ResultQuery<T extends QueryResultSet<?>> extends LocalQuery<T> {
}
