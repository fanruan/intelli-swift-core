package com.fr.swift.query.info.group.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.info.bean.type.PostQueryType;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQueryInfo implements PostQueryInfo {

    private List<MatchFilter> matchFilterList;

    public TreeFilterQueryInfo(List<MatchFilter> matchFilterList) {
        this.matchFilterList = matchFilterList;
    }

    /**
     * 对应NodeResultSet非叶子节点上面的过滤
     *
     * @return
     */
    public List<MatchFilter> getMatchFilterList() {
        return matchFilterList;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_FILTER;
    }
}
