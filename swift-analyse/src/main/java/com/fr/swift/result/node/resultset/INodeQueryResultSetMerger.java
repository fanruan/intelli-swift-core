package com.fr.swift.result.node.resultset;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

/**
 * Created by lyon on 2018/12/24.
 */
public interface INodeQueryResultSetMerger<T extends SwiftNode> extends QueryResultSetMerger<QueryResultSet<GroupPage>> {
}
