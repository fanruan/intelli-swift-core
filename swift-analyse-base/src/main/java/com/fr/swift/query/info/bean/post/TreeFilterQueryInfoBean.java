package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private Map<String, FilterInfoBean> filterInfoMap;

    {
        type = PostQueryType.TREE_FILTER;
    }

    public Map<String, FilterInfoBean> getFilterInfoMap() {
        return filterInfoMap;
    }

    public void setFilterInfoMap(Map<String, FilterInfoBean> filterInfoMap) {
        this.filterInfoMap = filterInfoMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_FILTER;
    }
}
