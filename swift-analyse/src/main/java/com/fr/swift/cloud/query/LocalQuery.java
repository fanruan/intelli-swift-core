package com.fr.swift.cloud.query;

import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/11/29.
 * 本地查询
 */
public interface LocalQuery<T extends QueryResultSet<?>> extends Query<T> {
}
