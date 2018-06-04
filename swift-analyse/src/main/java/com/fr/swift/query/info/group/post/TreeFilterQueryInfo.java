package com.fr.swift.query.info.group.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.post.PostQueryType;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQueryInfo implements PostQueryInfo {

    private Map<String, MatchFilter> matchFilterMap;

    public TreeFilterQueryInfo(Map<String, MatchFilter> matchFilterMap) {
        this.matchFilterMap = matchFilterMap;
    }

    public Map<String, MatchFilter> getMatchFilterMap() {
        return matchFilterMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_FILTER;
    }
}
