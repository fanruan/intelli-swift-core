package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * todo 尝试整理结构化查询的属性接口
 * <p>
 * Created by Lyon on 2018/6/2.
 */
public class GroupQueryInfoBean extends AbstractSingleTableQueryInfoBean {

    @JsonProperty
    private List<MetricBean> metricBeans = new ArrayList<MetricBean>(0);
    @JsonProperty
    private List<PostQueryInfoBean> postQueryInfoBeans = new ArrayList<PostQueryInfoBean>(0);

    {
        queryType = QueryType.GROUP;
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
