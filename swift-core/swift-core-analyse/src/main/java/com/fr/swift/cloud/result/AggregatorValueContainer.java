package com.fr.swift.cloud.result;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/16.
 * 保存聚合结果的容器
 */
public interface AggregatorValueContainer extends Serializable {

    void setAggregatorValue(int key, AggregatorValue value);

    AggregatorValue getAggregatorValue(int key);


    AggregatorValue[] getAggregatorValue();

    void setAggregatorValue(AggregatorValue[] aggregatorValues);
}
