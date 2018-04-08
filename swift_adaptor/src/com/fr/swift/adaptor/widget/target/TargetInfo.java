package com.fr.swift.adaptor.widget.target;

import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.result.node.cal.TargetCalculatorInfo;

import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetInfo {

    private int targetLength;
    private List<Metric> metrics;
    private List<TargetCalculatorInfo> targetCalculatorInfoList;
    private List<Integer> indexesOfTargetsForShow;

    public TargetInfo(List<Metric> metrics, List<TargetCalculatorInfo> targetCalculatorInfoList,
                      List<Integer> indexesOfTargetsForShow) {
        this.metrics = metrics;
        this.targetCalculatorInfoList = targetCalculatorInfoList;
        this.indexesOfTargetsForShow = indexesOfTargetsForShow;
        this.targetLength = metrics.size() + targetCalculatorInfoList.size();
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
    public List<TargetCalculatorInfo> getTargetCalculatorInfoList() {
        return targetCalculatorInfoList;
    }

    /**
     * 所有计算结果Number[targetLength]里面要展示的指标的索引
     *
     * @return
     */
    public List<Integer> getIndexesOfTargetsForShow() {
        return indexesOfTargetsForShow;
    }
}
