package com.fr.swift.query.info.remote;

import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Lyon on 2018/6/5.
 */
public interface RemoteQueryInfo<T extends SwiftResultSet> extends QueryInfo<T> {

    QueryInfo<T> getQueryInfo();
}
