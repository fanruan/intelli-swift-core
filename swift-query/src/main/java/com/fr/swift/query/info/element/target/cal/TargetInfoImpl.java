package com.fr.swift.query.info.element.target.cal;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.TargetInfo;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetInfoImpl that = (TargetInfoImpl) o;

        if (targetLength != that.targetLength) return false;
        if (metrics != null ? !metrics.equals(that.metrics) : that.metrics != null) return false;
        if (groupTargets != null ? !groupTargets.equals(that.groupTargets) : that.groupTargets != null) return false;
        if (targetsForShowList != null ? !targetsForShowList.equals(that.targetsForShowList) : that.targetsForShowList != null)
            return false;
        return resultAggregators != null ? resultAggregators.equals(that.resultAggregators) : that.resultAggregators == null;
    }

    @Override
    public int hashCode() {
        int result = targetLength;
        result = 31 * result + (metrics != null ? metrics.hashCode() : 0);
        result = 31 * result + (groupTargets != null ? groupTargets.hashCode() : 0);
        result = 31 * result + (targetsForShowList != null ? targetsForShowList.hashCode() : 0);
        result = 31 * result + (resultAggregators != null ? resultAggregators.hashCode() : 0);
        return result;
    }
}
