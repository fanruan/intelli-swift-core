package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.List;

/**
 * Created by 小灰灰 on 2017/7/7.
 */
public interface NodeCreator {
    Node createNode(int sumLen);

    Node createNode(Object data, int sumLength);

    List<TargetAndKey> createTargetAndKeyList(TargetAndKey targetAndKey);

    MetricMergeResult createMetricMergeResult(Object data, int sumLen, GroupValueIndex[] gvis);

    MetricMergeResult convertMetricMergeResult(MetricMergeResult node);

    void sumCalculateMetrics(List<TargetCalculator> calculatorList, List<CalCalculator> calCalculators, MetricMergeResult rootNode);

    void copySumValue(Node node, MetricMergeResult mergeResult);
}
