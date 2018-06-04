package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.NodeType;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<MatchFilter> matchFilterList;

    public TreeFilterQuery(PostQuery<NodeResultSet> query, List<MatchFilter> matchFilterList) {
        this.query = query;
        this.matchFilterList = matchFilterList;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        // 先做节点合计，再做过滤
        GroupNodeAggregateUtils.aggregateMetric(NodeType.GROUP, mergeResult.getRowGlobalDictionaries().size(),
                (GroupNode) mergeResult.getNode(), mergeResult.getAggregators());
        NodeFilter.filter(mergeResult.getNode(), matchFilterList);
        return new NodeMergeResultSetImpl((GroupNode) mergeResult.getNode(),
                mergeResult.getRowGlobalDictionaries(), mergeResult.getAggregators());
    }
}
