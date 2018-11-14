package com.fr.swift.query.info.group.post;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.info.bean.type.PostQueryType;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeAggregationQueryInfo implements PostQueryInfo {

    private List<Aggregator> aggregators;

    public TreeAggregationQueryInfo(List<Aggregator> aggregators) {
        this.aggregators = aggregators;
    }

    public List<Aggregator> getAggregators() {
        return aggregators;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.TREE_AGGREGATION;
    }
}
