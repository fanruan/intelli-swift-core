package com.fr.swift.query.info.group.post;

import com.fr.swift.query.post.PostQueryType;
import com.fr.swift.query.sort.Sort;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQueryInfo implements PostQueryInfo {

    private Map<String, Sort> sortMap;

    public TreeSortQueryInfo(Map<String, Sort> sortMap) {
        this.sortMap = sortMap;
    }

    public Map<String, Sort> getSortMap() {
        return sortMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_SORT;
    }
}
