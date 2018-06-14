package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * todo 尝试整理结构化查询的属性接口
 * <p>
 * Created by Lyon on 2018/6/2.
 */
public class GroupQueryBean extends AbstractSingleTableQueryBean {

    @JsonProperty
    private List<MetricBean> metricBeans;
    @JsonProperty
    private List<PostQueryInfoBean> postQueryInfoBeans;

    public QueryType getQueryType() {
        return queryType;
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
