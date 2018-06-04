package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<Sort> sortList;

    public TreeSortQuery(PostQuery<NodeResultSet> query, List<Sort> sortList) {
        this.query = query;
        this.sortList = sortList;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        NodeSorter.sort(mergeResult.getNode(), sortList);
        return mergeResult;
    }
}
