package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.result.SwiftNode;

import java.util.Iterator;

/**
 * 用于处理同时存在可遍历value和单个value时的合并
 *
 * @author yee
 * @date 2019-06-26
 */
public interface AggregatorValueCombiner {
    /**
     * 是否需要合并
     *
     * @return
     */
    boolean isNeedCombine();

    /**
     * 给每位设置
     *
     * @param i
     * @param value
     */
    void setValue(int i, AggregatorValue value);

    /**
     * 取出合并的Iterator
     *
     * @return
     */
    Iterator<SwiftNode> getSwiftNodeIterator(int depth);

    /**
     * 不需要合并直接取出来就可以了
     *
     * @return
     */
    AggregatorValue[] getAggregatorValue();
}
