package com.fr.swift.query.info.bean.query;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.aggregation.FunnelFunctionBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryBean extends AbstractSingleTableQueryInfoBean {

    {
        queryType = QueryType.FUNNEL;
    }

    @JsonProperty
    private FunnelFunctionBean aggregation;
    @JsonProperty
    private List<PostQueryInfoBean> postAggregations;

    public FunnelQueryBean() {
    }

    public FunnelQueryBean(FunnelFunctionBean aggregation) {
        this.aggregation = aggregation;
    }

    public FunnelQueryBean(FunnelFunctionBean aggregation, List<PostQueryInfoBean> postAggregations) {
        this.aggregation = aggregation;
        this.postAggregations = postAggregations;
    }

    public FunnelFunctionBean getAggregation() {
        return aggregation;
    }

    public void setAggregation(FunnelFunctionBean aggregation) {
        this.aggregation = aggregation;
    }

    public List<PostQueryInfoBean> getPostAggregations() {
        return postAggregations;
    }

    public void setPostAggregations(List<PostQueryInfoBean> postAggregations) {
        this.postAggregations = postAggregations;
    }
}

