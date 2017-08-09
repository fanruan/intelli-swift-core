package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.cal.analyze.cal.sssecret.XMetricMergeResult;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/7/10.
 */
public class XLeftNodeCreator implements NodeCreator {
    private int topLen;

    public XLeftNodeCreator(int topLen) {
        this.topLen = topLen;
    }

    @Override
    public Node createNode(int sumLen) {
        return new XLeftNode(sumLen, topLen);
    }

    @Override
    public Node createNode(Object data, int sumLength) {
        return new XLeftNode(data, sumLength, topLen);
    }

    @Override
    public List<TargetAndKey> createTargetAndKeyList(TargetAndKey targetAndKey) {
        List<TargetAndKey> list = new ArrayList<TargetAndKey>();
        for (int i = 0; i < topLen; i++){
            TargetGettingKey key = targetAndKey.getTargetGettingKey();
            list.add(new TargetAndKey(targetAndKey.getTargetId(), targetAndKey.getCalculator(), new XTargetGettingKey(key.getTargetIndex(), i, key.getTargetName())));
        }
        return list;
    }

    @Override
    public MetricMergeResult createMetricMergeResult(Object data, int sumLen, GroupValueIndex[] gvis) {
        return new XMetricMergeResult(data, sumLen, gvis, topLen);
    }

    @Override
    public MetricMergeResult convertMetricMergeResult(MetricMergeResult node) {
        return new XMetricMergeResult(node.getData(), node.getSummaryValue().length, node.getGvis(), topLen);
    }

    @Override
    public void sumCalculateMetrics(List<TargetCalculator> calculatorList, List<CalCalculator> calCalculators, MetricMergeResult rootNode) {
        CubeIndexLoader.calculateXTargets(calculatorList, calCalculators, (XMetricMergeResult)rootNode, topLen);
    }

    @Override
    public void copySumValue(Node node, MetricMergeResult mergeResult) {
        if (mergeResult != null){
            node.setSummaryValue(mergeResult.getSummaryValue());
            if (mergeResult instanceof XMetricMergeResult){
                ((BIXLeftNode)node).setXValue(((BIXLeftNode)mergeResult).getXValue());
            }
        }
    }

}
