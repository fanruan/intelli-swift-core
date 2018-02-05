package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;

/**
 * Created by pony on 2017/10/16.
 * 保存聚合结果的容器
 */
public interface AggregatorValueContainer {

    void setAggregatorValue(TargetGettingKey key, AggregatorValue value);

    AggregatorValue getAggregatorValue(TargetGettingKey key);

    AggregatorValue[] getAggregatorValue();

    void setAggregatorValue(AggregatorValue[] aggregatorValues);
}
