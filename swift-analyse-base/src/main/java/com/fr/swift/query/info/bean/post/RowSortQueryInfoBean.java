package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class RowSortQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private List<SortBean> sortBeans;

    {
        type = PostQueryType.ROW_SORT;
    }

    public RowSortQueryInfoBean() {
    }

    public RowSortQueryInfoBean(List<SortBean> sortBeans) {
        this.sortBeans = sortBeans;
    }

    public List<SortBean> getSortBeans() {
        return sortBeans;
    }

    public void setSortBeans(List<SortBean> sortBeans) {
        this.sortBeans = sortBeans;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.ROW_SORT;
    }
}
