package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;

import java.util.List;

/**
 * AggregatorValueRow 用于替换原来的AggregatorValue[]
 * @author yee
 * @date 2019-06-24
 */
public interface AggregatorValueRow<T extends AggregatorValue> extends Row {

    /**
     * 给该行的某一位设置
     *
     * @param i
     * @param value
     */
    void setValue(int i, T value);

    /**
     * 直接返回T写代码的时候不用指定Class很爽
     * @param i
     * @return
     */
    @Override
    T getValue(int i);

    /**
     * aggregatorValue转成数据
     *
     * @return
     */
    List<Object> data();
}
