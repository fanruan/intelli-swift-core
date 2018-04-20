package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;

import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetInfo {

    private int targetLength;
    private List<Metric> metrics;
    private List<GroupTarget> groupTargets;
    private List<ResultTarget> targetsForShowList;
    private List<Aggregator> aggregatorListOfTargetsForShow;

    public TargetInfo(List<Metric> metrics, List<GroupTarget> groupTargets,
                      List<ResultTarget> targetsForShowList, List<Aggregator> aggregatorListOfTargetsForShow) {
        this.metrics = metrics;
        this.groupTargets = groupTargets;
        this.targetsForShowList = targetsForShowList;
        this.aggregatorListOfTargetsForShow = aggregatorListOfTargetsForShow;
        this.targetLength = metrics.size() + groupTargets.size();
    }

    /**
     * 整个过程中计算的所有指标的长度，包括所有要展示的指标和一些为了计算而间接计算的指标
     *
     * @return
     */
    public int getTargetLength() {
        return targetLength;
    }

    /**
     * 需要聚合的指标
     *
     * @return
     */
    public List<Metric> getMetrics() {
        return metrics;
    }

    /**
     * 要根据聚合结果或者已经计算好的计算指标来计算的计算指标
     *
     * @return
     */
    public List<GroupTarget> getGroupTargets() {
        return groupTargets;
    }

    /**
     * 所有计算结果Number[targetLength]里面要展示的指标的索引
     *
     * @return
     */
    public List<ResultTarget> getTargetsForShowList() {
        return targetsForShowList;
    }

    public List<Aggregator> getAggregatorListOfTargetsForShow() {
        return aggregatorListOfTargetsForShow;
    }
}
