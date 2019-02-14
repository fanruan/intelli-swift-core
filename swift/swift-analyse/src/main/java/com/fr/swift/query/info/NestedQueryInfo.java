package com.fr.swift.query.info;

import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.result.SwiftResultSet;

import java.util.List;

/**
 * Created by Lyon on 2018/6/6.
 */
public interface NestedQueryInfo<T extends SwiftResultSet> extends QueryInfo<T> {

    QueryInfo<T> getSubQueryInfo();

    List<PostQueryInfo> getPostQueryInfoList();
}
