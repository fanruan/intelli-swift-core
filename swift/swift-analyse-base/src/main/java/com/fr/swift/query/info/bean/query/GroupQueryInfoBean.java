package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/2.
 */
public class GroupQueryInfoBean extends AbstractSingleTableQueryInfoBean {

    @JsonProperty
    private List<MetricBean> aggregations = new ArrayList<MetricBean>(0);
    @JsonProperty
    private List<PostQueryInfoBean> postAggregations = new ArrayList<PostQueryInfoBean>(0);

    {
        queryType = QueryType.GROUP;
    }

    public List<MetricBean> getAggregations() {
        return aggregations;
    }

    public void setAggregations(List<MetricBean> aggregations) {
        this.aggregations = aggregations;
    }

    public List<PostQueryInfoBean> getPostAggregations() {
        return postAggregations;
    }

    public void setPostAggregations(List<PostQueryInfoBean> postAggregations) {
        this.postAggregations = postAggregations;
    }
}
