package com.fr.swift.query.adapter.target;

import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.cal.ResultTarget;
import com.fr.swift.query.aggregator.Aggregator;

import java.util.List;

/**
 * Created by Lyon on 2018/4/23.
 */
public interface TargetInfo {

    /**
     * 整个过程中计算的所有指标的长度，包括所有要展示的指标和一些为了计算而间接计算的指标
     *
     * @return
     */
    int getTargetLength();

    /**
     * 需要聚合的指标
     *
     * @return
     */
    List<Metric> getMetrics();

    /**
     * 聚合指标用到的聚合器，合并SegmentGroupQuery与SegmentGroupQuery、GroupResultQuery与GroupResultQuery之间的结果
     *
     * @return
     */
    List<Aggregator> getAggregatorListOfMetrics();

    /**
     * 要根据聚合结果或者已经计算好的计算指标来计算的计算指标
     *
     * @return
     */
    List<GroupTarget> getGroupTargets();

    /**
     * 用于结果合并的聚合器，这是的结果是metrics + groupTargets
     *
     * @return
     */
    List<Aggregator> getAggregatorListForResultMerging();

    /**
     * 所有计算结果Number[targetLength]里面要展示的指标的索引
     *
     * @return
     */
    List<ResultTarget> getTargetsForShowList();
}
