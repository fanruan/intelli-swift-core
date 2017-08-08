package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.field.target.calculator.sum.AbstractSummaryCalculator;
import com.fr.bi.report.result.CalculatorType;
import com.fr.bi.report.result.TargetCalculator;

import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/8/8.
 */
public class PartNodeSummarizing {
    private Node node;
    protected Map<GroupUtils.NodeKey, List<GroupUtils.SummaryCountCal>> partNodeMap;

    public PartNodeSummarizing(Node node, Map<GroupUtils.NodeKey, List<GroupUtils.SummaryCountCal>> partNodeMap) {
        this.node = node;
        this.partNodeMap = partNodeMap;
    }

    protected void sum(){
        sum(node);
    }

    protected void sum(Node node) {
        //没有child的就不用汇总了
        if (node.getChildLength() == 0) {
            return;
        }
        for (int i = 0; i < node.getChildLength(); i++) {
            Node child = node.getChild(i);
            sum(child);
            List<GroupUtils.SummaryCountCal> list = partNodeMap.get(new GroupUtils.NodeKey(node));
            for (GroupUtils.SummaryCountCal cal: list) {
                TargetAndKey targetAndKey = cal.getTargetAndKey();
                Number value = node.getSummaryValue(targetAndKey.getTargetGettingKey());
                if (value == null) {
                    node.setSummaryValue(targetAndKey.getTargetGettingKey(), child.getSummaryValue(targetAndKey.getTargetGettingKey()));
                } else {
                    Number childValue = child.getSummaryValue(targetAndKey.getTargetGettingKey());
                    if (childValue != null) {
                        TargetCalculator calculator = targetAndKey.getCalculator();
                        if (calculator.getCalculatorType() == CalculatorType.SUM_DETAIL){
                            node.setSummaryValue(targetAndKey.getTargetGettingKey(), ((AbstractSummaryCalculator)calculator).createSumValue(value.doubleValue(), childValue.doubleValue()));
                        }
                    }
                }

            }
        }
    }
}
