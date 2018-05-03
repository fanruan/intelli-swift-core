package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.aggregator.Aggregator;

import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetInfoImpl implements TargetInfo {

    private int targetLength;
    private List<Metric> metrics;
    private List<GroupTarget> groupTargets;
    private List<ResultTarget> targetsForShowList;
    private List<Aggregator> resultAggregators;

    public TargetInfoImpl(int targetLength, List<Metric> metrics, List<GroupTarget> groupTargets,
                          List<ResultTarget> targetsForShowList, List<Aggregator> resultAggregators) {
        this.targetLength = targetLength;
        this.metrics = metrics;
        this.groupTargets = groupTargets;
        this.targetsForShowList = targetsForShowList;
        this.resultAggregators = resultAggregators;
    }

    @Override
    public int getTargetLength() {
        return targetLength;
    }

    @Override
    public List<Metric> getMetrics() {
        return metrics;
    }

    @Override
    public List<GroupTarget> getGroupTargets() {
        return groupTargets;
    }

    @Override
    public List<Aggregator> getResultAggregators() {
        return resultAggregators;
    }

    @Override
    public List<ResultTarget> getTargetsForShowList() {
        return targetsForShowList;
    }
}
