package com.fr.swift.result.node.resultset;

import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2018/12/24.
 */
public interface INodeQueryResultSetMerger<T extends SwiftNode> extends QueryResultSetMerger<Pair<T, List<Map<Integer, Object>>>, NodeMergeQRS<T>> {
}
