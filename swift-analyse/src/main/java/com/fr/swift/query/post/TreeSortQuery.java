package com.fr.swift.query.post;

import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private Map<String, Sort> sortMap;

    public TreeSortQuery(PostQuery<NodeResultSet> query, Map<String, Sort> sortMap) {
        this.query = query;
        this.sortMap = sortMap;
    }

    @Override
    public NodeResultSet getQueryResult() {
        return null;
    }
}
