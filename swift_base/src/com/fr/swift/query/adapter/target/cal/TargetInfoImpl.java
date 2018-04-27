package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.aggregator.Aggregator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetInfoImpl implements TargetInfo {

    private int targetLength;
    private List<Metric> metrics;
    private List<GroupTarget> groupTargets;
    private List<ResultTarget> targetsForShowList;
    private List<Aggregator> aggregatorListForResultTargetMerging;

    public TargetInfoImpl(List<Metric> metrics, List<GroupTarget> groupTargets,
                          List<ResultTarget> targetsForShowList, List<Aggregator> aggregatorListForResultTargetMerging) {
        this.metrics = metrics;
        this.groupTargets = groupTargets;
        this.targetsForShowList = targetsForShowList;
        this.aggregatorListForResultTargetMerging = aggregatorListForResultTargetMerging;
        this.targetLength = metrics.size() + groupTargets.size();
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
    public List<Aggregator> getAggregatorListOfMetrics() {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        for (Metric metric : metrics){
            aggregators.add(metric.getAggregator());
        }
        return aggregators;
    }

    @Override
    public List<GroupTarget> getGroupTargets() {
        return groupTargets;
    }

    @Override
    public List<Aggregator> getAggregatorListForResultMerging() {
        return aggregatorListForResultTargetMerging;
    }

    @Override
    public List<ResultTarget> getTargetsForShowList() {
        return targetsForShowList;
    }
}
