package com.fr.swift.query.result.serialize;

import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2018/12/29.
 */
public class NodeSerializableQRS extends BaseSerializableQRS<Pair<SwiftNode, List<Map<Integer, Object>>>> implements NodeMergeQRS<SwiftNode> {

    private static final long serialVersionUID = -1968765435028786800L;

    public NodeSerializableQRS(int fetchSize, QueryResultSetMerger merger,
                               Pair<SwiftNode, List<Map<Integer, Object>>> page, boolean originHasNextPage) {
        super(fetchSize, merger, page, originHasNextPage);
    }
}
