package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/7/10.
 */
public class GroupNodeCreator implements NodeCreator {
    @Override
    public Node createNode(int sumLen) {
        return new Node(sumLen);
    }

    @Override
    public Node createNode(Object data, int sumLength) {
        return new Node(data, sumLength);
    }

    @Override
    public List<TargetAndKey> createTargetAndKeyList(TargetAndKey targetAndKey) {
        List<TargetAndKey> list = new ArrayList<TargetAndKey>();
        list.add(targetAndKey);
        return list;
    }

    @Override
    public MetricMergeResult createMetricMergeResult(Object data, int sumLen, GroupValueIndex[] gvis) {
        return new MetricMergeResult(data, sumLen, gvis);
    }

    @Override
    public MetricMergeResult convertMetricMergeResult(MetricMergeResult node) {
        return node;
    }

    @Override
    public void sumCalculateMetrics(List<TargetCalculator> calculatorList, List<CalCalculator> calCalculators, MetricMergeResult rootNode) {
        CubeIndexLoader.calculateTargets(calculatorList, calCalculators, rootNode);
    }

    @Override
    public void copySumValue(Node node, MetricMergeResult mergeResult) {
        if (mergeResult != null){
            node.setSummaryValue(mergeResult.getSummaryValue());
        }
    }
}
