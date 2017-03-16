package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2017/1/17.
 */
public class PartConstructedRootDimensionGroup extends RootDimensionGroup {
    //最后一个需要构建结构的维度
    private int lastConstructedDimensionIndex;
    private MetricMergeResult rootNode;
    private int sortIndex = -1;
    private TargetAndKey sortTarget;
    private int sortType;
    private List<CalCalculator> configureRelatedCalculators;
    private DimensionFilter[] calculateMetricsDimensionFilters;

    public PartConstructedRootDimensionGroup() {
    }

    public PartConstructedRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, BISession session, boolean useRealData, int lastConstructedDimensionIndex, NameObject targetSort, List<CalCalculator> configureRelatedCalculators, DimensionFilter[] calculateMetricsDimensionFilters) {
        super(metricGroupInfoList, mergeIteratorCreators, session, useRealData);
        this.lastConstructedDimensionIndex = lastConstructedDimensionIndex;
        this.configureRelatedCalculators = configureRelatedCalculators;
        this.calculateMetricsDimensionFilters = calculateMetricsDimensionFilters;
        initRootNode();
        initSort(targetSort);
    }

    private void initSort(NameObject targetSort) {
        if (targetSort != null){
            sortType = (Integer)targetSort.getObject();
            for (int i = 0; i < metricGroupInfoList.size(); i++){
                MetricGroupInfo info = metricGroupInfoList.get(i);
                List<TargetAndKey> list = info.getSummaryList();
                for (TargetAndKey targetAndKey : list){
                    if (ComparatorUtils.equals(targetAndKey.getTargetId(), targetSort.getName())){
                        sortTarget = targetAndKey;
                        sortIndex = i;
                        return;
                    }
                }
            }
        }
    }

    private void initRootNode() {
        rootNode = new MetricMergeResult(null, root.getGvis());
        if(columns.length == 0){
            return;
        }
        if (MultiThreadManagerImpl.getInstance().isMultiCall()){
            multiThreadBuild();
        } else {
            singleThreadBuild();
        }
        sumCalculateMetrics();
        root.setChildren(rootNode.getChilds());
    }

    private void singleThreadBuild() {
        cal(rootNode, root, 0);
    }

    private void cal(MetricMergeResult node, NoneDimensionGroup childDimensionGroup, int level) {
        if (level >= lastConstructedDimensionIndex){
            return;
        }
        SingleDimensionGroup rootGroup =  childDimensionGroup.createSingleDimensionGroup(columns[level], getters[level], null, mergeIteratorCreators[level], useRealData);
        int index = 0;
        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
        while (result != MetricMergeResult.NULL){
            node.addChild(result);
            if (level < lastConstructedDimensionIndex){
                cal(result, rootGroup.getChildDimensionGroup(index), level + 1);
            }
            index++;
            result = rootGroup.getMetricMergeResultByWait(index);
        }
        sum(node);
    }

    private void multiThreadBuild() {
        new MultiThreadBuilder().build();
    }

    private void sumCalculateMetrics() {
        if (!configureRelatedCalculators.isEmpty()){
            List<TargetCalculator> calculatorList = new ArrayList<TargetCalculator>();
            for (MetricGroupInfo info : metricGroupInfoList){
                for (TargetAndKey key : info.getSummaryList()){
                    calculatorList.add(key.getCalculator());
                }
            }
            CubeIndexLoader.calculateTargets(calculatorList, configureRelatedCalculators, rootNode);
        }
    }

    private void sum(MetricMergeResult node) {
        GroupValueIndex[] pGvis = node.getGvis();
        GroupValueIndexOrHelper[] helpers = new GroupValueIndexOrHelper[pGvis.length];
        Arrays.fill(helpers, new GroupValueIndexOrHelper());
        for (Node child : node.getChilds()){
            MetricMergeResult result = (MetricMergeResult) child;
            GroupValueIndex[] gvis = result.getGvis();
            for (int i = 0; i < helpers.length; i++){
                helpers[i].add(gvis[i]);
            }
        }
        for (int i = 0; i < helpers.length; i++){
            pGvis[i] = helpers[i].compute();
        }
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
        if (deep <= lastConstructedDimensionIndex){
            return ng.createNodeSingleDimensionGroup(columns[deep], getters[deep], data, mergeIteratorCreators[deep], ng.getChildren(), sortTarget, sortIndex, sortType, calculateMetricsDimensionFilters[deep]);
        }
        return super.createSingleDimensionGroup(data, ng, deep);
    }

    private class MultiThreadBuilder{
        //每一层维度计算完成的数量
        private AtomicInteger[] count;
        //每一层维度被丢进线程池的数量
        private AtomicInteger[] size;

        private BIMultiThreadExecutor executor = MultiThreadManagerImpl.getInstance().getExecutorService();

        public void build(){
            int dimensionSize = lastConstructedDimensionIndex + 1;
            count = new AtomicInteger[dimensionSize];
            size = new AtomicInteger[dimensionSize];
            for (int i = 0; i < dimensionSize; i++) {
                count[i] = new AtomicInteger(0);
                size[i] = new AtomicInteger(0);
            }
            SingleDimensionGroup rootGroup =  root.createSingleDimensionGroup(columns[0], getters[0], null, mergeIteratorCreators[0], useRealData);
            int index = 0;
            MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
            while (result != MetricMergeResult.NULL){
                rootNode.addChild(result);
                size[0].incrementAndGet();
                executor.add(new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), 0));
                index++;
                result = rootGroup.getMetricMergeResultByWait(index);
            }
            //如果多线程计算没有结束，就等结束
            if (!allCompleted()) {
                executor.wakeUp();
                synchronized (this) {
                    if (!allCompleted()) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            sum(rootNode);
        }

        private boolean allCompleted() {
            for (int i = 0; i < count.length; i++) {
                if (count[i].get() != size[i].get() || !currentLevelAllAdded(i)) {
                    return false;
                }
            }
            return true;
        }

        private void checkComplete(int level) {
            if (currentLevelAllAdded(level)) {
                //完成了一个维度必须唤醒下线程，要不肯能会wait住死掉。
                executor.wakeUp();
                synchronized (this) {
                    //全部完成了就唤醒下wait的主线程
                    if (allCompleted()) {
                        this.notify();
                    }
                }
            }
        }

        //当前层的迭代器是否都执行完了
        private boolean currentLevelAllAdded(int level) {
            //最后一层没有迭代器
            if (level > lastConstructedDimensionIndex) {
                return false;
            }
            //执行完的迭代器的数量不等于0，并且等于上一层的丢进线程池的计算数量。
            return count[level].get() != 0 && count[level].get() == size[level].get();
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
                checkComplete(level);
            }

            private void cal(MetricMergeResult node, NoneDimensionGroup childDimensionGroup, int level) {
                if(level < lastConstructedDimensionIndex){
                    SingleDimensionGroup rootGroup =  childDimensionGroup.createSingleDimensionGroup(columns[level + 1], getters[level + 1], null, mergeIteratorCreators[level + 1], useRealData);
                    int index = 0;
                    MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
                    while (result != MetricMergeResult.NULL){
                        node.addChild(result);
                        executor.add(new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), level + 1));
                        size[level + 1].incrementAndGet();
                        index++;
                        result = rootGroup.getMetricMergeResultByWait(index);
                    }
                }
                sum(node);
                count[level].incrementAndGet();
            }
        }

    }


    @Override
    public IRootDimensionGroup createClonedRoot() {
        PartConstructedRootDimensionGroup root = (PartConstructedRootDimensionGroup) super.createClonedRoot();
        root.lastConstructedDimensionIndex = lastConstructedDimensionIndex;
        root.rootNode = rootNode;
        root.sortIndex = sortIndex;
        root.sortTarget = sortTarget;
        root.sortType = sortType;
        root.configureRelatedCalculators = configureRelatedCalculators;
        root.calculateMetricsDimensionFilters = calculateMetricsDimensionFilters;
        return root;
    }

    @Override
    protected IRootDimensionGroup createNew() {
        return new PartConstructedRootDimensionGroup();
    }
}
