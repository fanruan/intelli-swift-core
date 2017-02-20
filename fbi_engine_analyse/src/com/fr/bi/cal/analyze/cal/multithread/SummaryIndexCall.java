package com.fr.bi.cal.analyze.cal.multithread;

import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.NoneDimensionGroup;
import com.fr.bi.stable.report.result.TargetCalculator;

/**
 * Created by 小灰灰 on 2017/2/20.
 */
public class SummaryIndexCall extends SummaryCall {
    public SummaryIndexCall(Node node, NoneDimensionGroup noneDimensionGroup, TargetCalculator calculator) {
        super(node, noneDimensionGroup, calculator);
    }
    @Override
    public void cal() {
        if (calculator != null) {
            Number v = noneDimensionGroup.getSummaryValue(calculator);
            if (v != null) {
                node.setTargetGetter(calculator.createTargetGettingKey(), noneDimensionGroup.getRoot().getGroupValueIndex());
                node.setTargetIndex(calculator.createTargetGettingKey(), noneDimensionGroup.getRoot().getGroupValueIndex());
                node.setSummaryValue(calculator.createTargetGettingKey(), v);
            }
        }
    }
}
