package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private Map<String, SortBean> sortMap;

    {
        type = PostQueryType.TREE_SORT;
    }

    public Map<String, SortBean> getSortMap() {
        return sortMap;
    }

    public void setSortMap(Map<String, SortBean> sortMap) {
        this.sortMap = sortMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_SORT;
    }
}
