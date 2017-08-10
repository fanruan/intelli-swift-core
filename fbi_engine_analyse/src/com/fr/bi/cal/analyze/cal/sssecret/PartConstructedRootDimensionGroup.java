package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.exception.BIMemoryDataOutOfLimitException;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.general.NameObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2017/6/13.
 */
public class PartConstructedRootDimensionGroup extends ConstructedRootDimensionGroup {
    private int maxSize;
    private int size;

    private PartConstructedRootDimensionGroup() {
    }

    public PartConstructedRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, int sumLength, BISession session, boolean useRealData, NameObject[] dimensionTargetSort, List<CalCalculator> calCalculators, BIDimension[] filterDimension, boolean setIndex, boolean hasInSumMetric, BIMultiThreadExecutor executor, boolean calAllPage, NodeCreator nodeCreator, int maxSize) {
        super(metricGroupInfoList, mergeIteratorCreators, sumLength, session, useRealData, dimensionTargetSort, calCalculators, filterDimension, setIndex, hasInSumMetric, executor, nodeCreator, calAllPage);
        this.maxSize = maxSize;
    }

    protected void singleThreadBuild() {

        cal(rootNode, root, 0);
        sum(rootNode, -1);
    }

    private void cal(MetricMergeResult node, NoneDimensionGroup childDimensionGroup, int level) {
        if (level >= rowSize) {
            return;
        }
        SingleDimensionGroup rootGroup = childDimensionGroup.createSingleDimensionGroup(columns[level], getters[level], null, mergeIteratorCreators[level], useRealData);
        int index = 0;
        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
        while (result != MetricMergeResult.NULL) {
            if (size > maxSize){
                throw new BIMemoryDataOutOfLimitException();
            }
            node.addChild(result);
            size++;
            if (level < rowSize - 1) {
                cal(result, rootGroup.getChildDimensionGroup(index), level + 1);
            }
            //计算child之后才能sum，因为sum可能会cleargvi
            sum(result, level);
            index++;
            result = rootGroup.getMetricMergeResultByWait(index);
        }
    }

    protected void multiThreadBuild() {

        new PartMultiThreadBuilder().build();
    }

    protected class PartMultiThreadBuilder extends MultiThreadBuilder {
        private AtomicInteger size = new AtomicInteger(0);
        @Override
        protected void addTask(SingleChildCal cal) {
            if (size.incrementAndGet() > maxSize){
                throw new BIMemoryDataOutOfLimitException();
            }
            super.addTask(cal);
        }
    }


    @Override
    public IRootDimensionGroup createClonedRoot() {
        PartConstructedRootDimensionGroup root = (PartConstructedRootDimensionGroup) super.createClonedRoot();
        root.maxSize = maxSize;
        return root;
    }

    @Override
    protected IRootDimensionGroup createNew() {
        return new PartConstructedRootDimensionGroup();
    }

}
