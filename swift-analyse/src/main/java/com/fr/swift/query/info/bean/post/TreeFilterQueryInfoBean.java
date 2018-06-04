package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.post.PostQueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private Map<String, FilterInfo> filterInfoMap;

    public Map<String, FilterInfo> getFilterInfoMap() {
        return filterInfoMap;
    }

    public void setFilterInfoMap(Map<String, FilterInfo> filterInfoMap) {
        this.filterInfoMap = filterInfoMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_FILTER;
    }
}
