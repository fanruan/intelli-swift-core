package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.FilterInfoBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public abstract class AbstractSingleTableQueryBean extends AbstractQueryBean {

    @JsonProperty
    private String tableName;
    @JsonProperty
    private FilterInfoBean filterInfoBean;
    @JsonProperty
    private List<DimensionBean> dimensionBeans;
    @JsonProperty
    private List<SortBean> sortBeans;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public FilterInfoBean getFilterInfoBean() {
        return filterInfoBean;
    }

    public void setFilterInfoBean(FilterInfoBean filterInfoBean) {
        this.filterInfoBean = filterInfoBean;
    }

    public List<DimensionBean> getDimensionBeans() {
        return dimensionBeans;
    }

    public void setDimensionBeans(List<DimensionBean> dimensionBeans) {
        this.dimensionBeans = dimensionBeans;
    }

    public List<SortBean> getSortBeans() {
        return sortBeans;
    }

    public void setSortBeans(List<SortBean> sortBeans) {
        this.sortBeans = sortBeans;
    }
}
