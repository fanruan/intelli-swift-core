package com.fr.swift.query.adapter.target;


import com.fr.swift.query.aggregator.Aggregator;

/**
 * Created by pony on 2017/12/13.
 * 分组表的指标
 */
public interface GroupTarget extends Target {

    /**
     * 指标的结果合并
     *
     * @return
     */
    Aggregator getAggregator();
}
