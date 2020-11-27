package com.fr.swift.query.info.bean.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.LimitBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public abstract class AbstractSingleTableQueryInfoBean extends AbstractQueryInfoBean implements SingleInfoBean {

    @JsonProperty
    private String tableName;
    @JsonProperty
    private FilterInfoBean filter;
    @JsonProperty
    private List<DimensionBean> dimensions = new ArrayList<DimensionBean>(0);
    @JsonProperty
    private List<SortBean> sorts = new ArrayList<SortBean>(0);
    @JsonProperty
    private LimitBean limit;

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }

    @Override
    public List<DimensionBean> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<DimensionBean> dimensions) {
        this.dimensions = dimensions;
    }

    public List<SortBean> getSorts() {
        return sorts;
    }

    @Override
    public void setSorts(List<SortBean> sorts) {
        this.sorts = sorts;
    }


    public LimitBean getLimit() {
        return limit;
    }

    public void setLimit(LimitBean limit) {
        this.limit = limit;
    }
}
