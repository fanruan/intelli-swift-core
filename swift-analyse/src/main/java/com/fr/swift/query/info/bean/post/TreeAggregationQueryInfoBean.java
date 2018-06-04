package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.post.PostQueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeAggregationQueryInfoBean extends AbstractPostQueryInfoBean {

    @JsonProperty
    private Map<String, AggregatorType> aggregatorTypeMap;

    public Map<String, AggregatorType> getAggregatorTypeMap() {
        return aggregatorTypeMap;
    }

    public void setAggregatorTypeMap(Map<String, AggregatorType> aggregatorTypeMap) {
        this.aggregatorTypeMap = aggregatorTypeMap;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_AGGREGATION;
    }
}
