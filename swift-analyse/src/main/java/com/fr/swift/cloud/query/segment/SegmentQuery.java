package com.fr.swift.cloud.query.segment;

import com.fr.swift.cloud.query.LocalQuery;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/11/27.
 * 对一块数据的查询
 */
public interface SegmentQuery<T extends QueryResultSet<?>> extends LocalQuery<T> {
}
