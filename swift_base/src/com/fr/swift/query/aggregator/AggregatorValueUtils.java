package com.fr.swift.query.aggregator;

/**
 * Created by Lyon on 2018/5/2.
 */
public class AggregatorValueUtils {

    /**
     * 返回新创建的合并结果
     *
     * @param value
     * @param otherValue
     * @param aggregator
     * @param <T>
     * @return
     */
    public static <T extends AggregatorValue> T combine(T value, T otherValue, Aggregator<T> aggregator) {
        if (value == null && otherValue == null) {
            return null;
        }
        if (value == null) {
            return aggregator.createAggregatorValue(otherValue);
        }
        if (otherValue == null) {
            return aggregator.createAggregatorValue(value);
        }
        aggregator.combine(value, otherValue);
        return aggregator.createAggregatorValue(value);
    }
}
