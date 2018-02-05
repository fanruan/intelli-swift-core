package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2017/10/10.
 * 聚合之后的结果
 */
public interface AggregatorValue<T> {
    /**
     * 将聚合的结果转化为实际的值，比如去重计数的bitmap取size，平均值sum/count
     *
     * @return
     */
    double calculate();
}
