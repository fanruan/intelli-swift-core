package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * Created by Lyon on 2018/6/3.
 */
public class HavingFilterQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private String column;
    @JsonProperty
    private FilterInfoBean filter;

    {
        type = PostQueryType.HAVING_FILTER;
    }

    public HavingFilterQueryInfoBean() {
    }

    public HavingFilterQueryInfoBean(String column, FilterInfoBean filter) {
        this.column = column;
        this.filter = filter;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.HAVING_FILTER;
    }
}
