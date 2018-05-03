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
            return (T) otherValue.clone();
        }
        if (otherValue == null) {
            return (T) value.clone();
        }
        aggregator.combine(value, otherValue);
        return (T) value.clone();
    }
}
