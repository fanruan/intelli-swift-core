package com.fr.swift.query;


import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.query.query.Query;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by pony on 2017/12/12.
 */
public class EmptyQuery<T extends SwiftResultSet> implements Query<T> {
    @Override
    public T getQueryResult() throws SwiftSegmentAbsentException {
        throw new SwiftSegmentAbsentException("local segment missed");
    }
}
