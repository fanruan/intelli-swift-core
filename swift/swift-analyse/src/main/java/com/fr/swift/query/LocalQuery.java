package com.fr.swift.query;

import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/11/29.
 * 本地查询
 */
public interface LocalQuery<T extends QueryResultSet> extends Query<T> {
}
