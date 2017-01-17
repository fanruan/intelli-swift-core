package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;

import java.util.List;

/**
 * Created by 小灰灰 on 2017/1/17.
 */
public class PartConstructedRootDimensionGroup extends RootDimensionGroup {
    //最后一个需要构建结构的维度
    private int lastConstructedDimensinIndex;
    private MetricMergeResult rootNode;
    public PartConstructedRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, BISession session, boolean useRealData, int lastConstructedDimensinIndex) {
        super(metricGroupInfoList, mergeIteratorCreators, session, useRealData);
        this.lastConstructedDimensinIndex = lastConstructedDimensinIndex;
        initRootNode();
    }

    private void initRootNode() {
        rootNode = new MetricMergeResult(null, root.getGvis());
        SingleDimensionGroup rootGroup =  root.createSingleDimensionGroup(columns[0], getters[0], null, mergeIteratorCreators[0], useRealData);
        int index = 0;
        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
        while (result != MetricMergeResult.NULL){
            rootNode.addChild(result);
            new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), 1).cal();
            index++;
            result = rootGroup.getMetricMergeResultByWait(index);
        }
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
        if (deep < lastConstructedDimensinIndex){

        }
        return super.createSingleDimensionGroup(data, ng, deep);
    }

    private class SingleChildCal implements BISingleThreadCal{
        private MetricMergeResult node;
        private NoneDimensionGroup childDimensionGroup;
        private int level;

        public SingleChildCal(MetricMergeResult result, NoneDimensionGroup childDimensionGroup, int level) {
            this.node = result;
            this.childDimensionGroup = childDimensionGroup;
            this.level = level;
        }

        @Override
        public void cal() {
            cal(node, childDimensionGroup, level);
        }

        private void cal(MetricMergeResult node, NoneDimensionGroup childDimensionGroup, int level) {
            SingleDimensionGroup rootGroup =  childDimensionGroup.createSingleDimensionGroup(columns[level], getters[level], null, mergeIteratorCreators[level], useRealData);
            int index = 0;
            MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
            while (result != MetricMergeResult.NULL){
                node.addChild(result);
                if (level < lastConstructedDimensinIndex){
                    cal(result, rootGroup.getChildDimensionGroup(index), level + 1);
                }
                index++;
                result = rootGroup.getMetricMergeResultByWait(index);
            }
        }
    }
}
