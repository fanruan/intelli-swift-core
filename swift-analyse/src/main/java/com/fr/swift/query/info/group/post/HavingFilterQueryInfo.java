package com.fr.swift.query.info.group.post;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.post.PostQueryType;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class HavingFilterQueryInfo implements PostQueryInfo {

    private Map<String, FilterInfo> filterInfoMap;

    public HavingFilterQueryInfo(Map<String, FilterInfo> filterInfoMap) {
        this.filterInfoMap = filterInfoMap;
    }

    public Map<String, FilterInfo> getFilterInfoMap() {
        return filterInfoMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.HAVING_FILTER;
    }
}
