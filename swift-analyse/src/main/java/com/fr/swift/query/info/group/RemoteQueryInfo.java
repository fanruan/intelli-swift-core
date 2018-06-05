package com.fr.swift.query.info.group;

import com.fr.swift.query.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Lyon on 2018/6/5.
 */
public interface RemoteQueryInfo<T extends SwiftResultSet> extends QueryInfo<T> {

    QueryInfo<T> getQueryInfo();
}
