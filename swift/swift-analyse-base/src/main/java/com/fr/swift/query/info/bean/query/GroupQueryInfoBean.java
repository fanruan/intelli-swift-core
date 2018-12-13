package com.fr.swift.query.info.bean.query;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/2.
 */
public class GroupQueryInfoBean extends AbstractSingleTableQueryInfoBean implements Serializable {

    private static final long serialVersionUID = 8719563190390818951L;
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
