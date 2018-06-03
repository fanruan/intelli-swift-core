package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.QueryType;
import com.fr.swift.query.info.bean.DimensionBean;
import com.fr.swift.query.info.bean.FilterInfoBean;
import com.fr.swift.query.info.bean.MetricBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * todo 尝试整理结构化查询的属性接口
 * <p>
 * Created by Lyon on 2018/6/2.
 */
public class GroupQueryBean extends AbstractQueryBean {

    @JsonProperty
    private String tableName;
    @JsonProperty
    private FilterInfoBean filterInfoBean;  // 这个明细过滤是不是写到dimensionBean里面去好一点呢？
    @JsonProperty
    private List<DimensionBean> dimensionBeans;
    @JsonProperty
    private List<MetricBean> metricBeans;
    @JsonProperty
    private List<PostQueryInfoBean> postQueryInfoBeans;

    public QueryType getType() {
        return type;
    }

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

    public List<MetricBean> getMetricBeans() {
        return metricBeans;
    }

    public void setMetricBeans(List<MetricBean> metricBeans) {
        this.metricBeans = metricBeans;
    }

    public List<PostQueryInfoBean> getPostQueryInfoBeans() {
        return postQueryInfoBeans;
    }

    public void setPostQueryInfoBeans(List<PostQueryInfoBean> postQueryInfoBeans) {
        this.postQueryInfoBeans = postQueryInfoBeans;
    }
}
