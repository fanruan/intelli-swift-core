package com.fr.swift.query.segment;

import com.fr.swift.query.LocalQuery;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/11/27.
 * 对一块数据的查询
 */
public interface SegmentQuery<T extends QueryResultSet> extends LocalQuery<T> {
}
