package com.fr.swift.cloud.query.aggregator;

import java.util.List;

/**
 * 可展开的AggregatorValue
 *
 * @author yee
 * @date 2019-07-10
 */
public interface ExtensionAggregatorValue<T> extends AggregatorValue<T> {
    /**
     * 计算并展开计算结果
     *
     * @return
     */
    List calculateAndExtension();
}
