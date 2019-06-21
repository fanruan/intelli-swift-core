package com.fr.swift.query.result.serialize;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

/**
 * Created by lyon on 2018/12/29.
 */
public class NodeSerializableQRS extends BaseSerializableQRS<GroupPage> implements QueryResultSet<GroupPage> {

    private static final long serialVersionUID = -1968765435028786800L;

    public NodeSerializableQRS(int fetchSize, QueryResultSetMerger<QueryResultSet<GroupPage>> merger,
                               GroupPage page, boolean originHasNextPage) {
        super(fetchSize, merger, page, originHasNextPage);
    }
}
