package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/16.
 * 保存聚合结果的容器
 */
public interface AggregatorValueContainer extends Serializable {

    /**
     * 方便单行值的
     *
     * @return
     */
    AggregatorValueRow asSingleAggRowValue();

    AggregatorValueSet getAggregatorValue();

    void setAggregatorValue(AggregatorValueSet aggregatorValues);
}
