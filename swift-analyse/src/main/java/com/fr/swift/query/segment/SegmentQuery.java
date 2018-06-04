package com.fr.swift.query.segment;

import com.fr.swift.query.LocalQuery;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by pony on 2017/11/27.
 * 对一块数据的查询
 */
public interface SegmentQuery<T extends SwiftResultSet> extends LocalQuery<T> {
}
