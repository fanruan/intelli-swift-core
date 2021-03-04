package com.fr.swift.cloud.query;


import com.fr.swift.cloud.exception.SwiftSegmentAbsentException;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/12/12.
 */
public class EmptyQuery<T extends QueryResultSet<?>> implements Query<T> {
    @Override
    public T getQueryResult() throws SwiftSegmentAbsentException {
        throw new SwiftSegmentAbsentException("local segment missed");
    }
}
