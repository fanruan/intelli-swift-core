package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCall;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2017/1/17.
 */
public class ConstructedRootDimensionGroup extends RootDimensionGroup {
    private MetricMergeResult rootNode;
    private NameObject targetSort;
    private List<CalCalculator> calCalculators;
    private BIDimension[] filterDimension;
    private boolean setIndex;
    private boolean hasInSumMetric;
    private int sortType;
    private TargetAndKey sortTarget;

    public ConstructedRootDimensionGroup() {
    }

    public ConstructedRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, BISession session, boolean useRealData,
                                         NameObject targetSort, List<CalCalculator> calCalculators, BIDimension[] filterDimension, boolean setIndex, boolean hasInSumMetric) {
        super(metricGroupInfoList, mergeIteratorCreators, session, useRealData);
        this.calCalculators = calCalculators;
        this.filterDimension = filterDimension;
        this.setIndex = setIndex;
        this.targetSort = targetSort;
        this.hasInSumMetric = hasInSumMetric;
        initSort();
        initRootNode();
    }

    private void initSort() {
        if (targetSort != null) {
            sortType = (Integer) targetSort.getObject();
            for (int i = 0; i < metricGroupInfoList.size(); i++) {
                MetricGroupInfo info = metricGroupInfoList.get(i);
                List<TargetAndKey> list = info.getSummaryList();
                for (TargetAndKey targetAndKey : list) {
                    if (ComparatorUtils.equals(targetAndKey.getTargetId(), targetSort.getName())) {
                        sortTarget = targetAndKey;
                        return;
                    }
                }
            }
        }
    }

    private void initRootNode() {
        rootNode = new MetricMergeResult(null, root.getGvis());
        if (columns.length == 0) {
            return;
        }
        if (MultiThreadManagerImpl.getInstance().isMultiCall()) {
            multiThreadBuild();
        } else {
            singleThreadBuild();
        }
        //先计算一遍配置类计算与计算指标，过滤排序完还要再计算一次
        sumCalculateMetrics();
        Map<String, TargetCalculator> calculatorMap = new HashMap<String, TargetCalculator>();
        for (List<TargetAndKey> list : summaryLists) {
            for (TargetAndKey key : list) {
                calculatorMap.put(key.getTargetId(), key.getCalculator());
            }
        }
        for (CalCalculator calCalculator : calCalculators) {
            calculatorMap.put(calCalculator.getName(), calCalculator);
        }
        if (filterDimension != null || targetSort != null) {
            filterAndSort(rootNode, 0, calculatorMap);
            if (filterDimension != null) {
                reSum();
            }
            sumCalculateMetrics();
        }
        root.setChildren(rootNode.getChilds());
        if (setIndex){
            root.setGvis(rootNode.getGvis());
            for (int i = 0; i < metricGroupInfoList.size(); i++){
                metricGroupInfoList.get(i).setFilterIndex(root.getGvis()[i]);
            }
        }
    }

    //过滤排序一起撸了，省得遍历两次node结构，有需要可以改成多线程的
    private void filterAndSort(MetricMergeResult node, int deep, Map<String, TargetCalculator> calculatorMap) {
        if (deep < rowSize) {
            DimensionFilter filter = filterDimension == null ? null : filterDimension[deep].getFilter();
            if (filter != null || targetSort != null) {
                List<Node> children = filterAndSort(node.getChilds(), deep, calculatorMap);
                node.clearChildren();
                for (Node n : children) {
                    node.addChild(n);
                }
            }
            for (Node n : node.getChilds()) {
                filterAndSort((MetricMergeResult) n, deep + 1, calculatorMap);
            }
        }
    }

    private List<Node> filterAndSort(List<Node> children, int deep, Map<String, TargetCalculator> calculatorMap) {
        DimensionFilter filter = filterDimension == null ? null : filterDimension[deep].getFilter();
        List<Node> results = new ArrayList<Node>();
        if (filter == null) {
            results = children;
        } else {
            for (Node result : children) {
                if (filter.showNode(result, calculatorMap, session.getLoader())) {
                    results.add(result);
                }
            }
        }
        if (targetSort != null) {
            Collections.sort(results, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    Number v1 = o1.getSummaryValue(sortTarget.getTargetGettingKey());
                    Number v2 = o2.getSummaryValue(sortTarget.getTargetGettingKey());
                    if (v1 == v2) {
                        return 0;
                    }
                    if (v1 == null) {
                        return 1;
                    }
                    if (v2 == null) {
                        return -1;
                    }
                    if (v1.doubleValue() == v2.doubleValue()) {
                        return 0;
                    }
                    boolean v = v1.doubleValue() < v2.doubleValue();
                    return (sortType == BIReportConstant.SORT.ASC) == v ? -1 : 1;
                }
            });
        }
        return results;
    }


    //有过滤要重新汇总下
    private void reSum() {
        List<TargetGettingKey> keys = new ArrayList<TargetGettingKey>();
        for (List<TargetAndKey> list : summaryLists) {
            for (TargetAndKey key : list) {
                keys.add(key.getTargetGettingKey());
            }
        }
        NodeSummarizing summarizing = hasInSumMetric ? new NodeGVISummarizing(rootNode, keys.toArray(new TargetGettingKey[keys.size()])) : new NodeSummarizing(rootNode, keys.toArray(new TargetGettingKey[keys.size()]));
        summarizing.sum();
    }

    private void singleThreadBuild() {
        cal(rootNode, root, 0);
    }

    private void cal(MetricMergeResult node, NoneDimensionGroup childDimensionGroup, int level) {
        if (level >= rowSize) {
            return;
        }
        SingleDimensionGroup rootGroup = childDimensionGroup.createSingleDimensionGroup(columns[level], getters[level], null, mergeIteratorCreators[level], useRealData);
        int index = 0;
        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
        while (result != MetricMergeResult.NULL) {
            node.addChild(result);
            if (level < rowSize - 1) {
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
        if (!calCalculators.isEmpty()) {
            List<TargetCalculator> calculatorList = new ArrayList<TargetCalculator>();
            for (MetricGroupInfo info : metricGroupInfoList) {
                for (TargetAndKey key : info.getSummaryList()) {
                    calculatorList.add(key.getCalculator());
                }
            }
            List<CalCalculator> calCalculators = new ArrayList<CalCalculator>();
            calCalculators.addAll(this.calCalculators);
            CubeIndexLoader.calculateTargets(calculatorList, calCalculators, rootNode);
        }
    }

    private void sum(MetricMergeResult node) {
        GroupValueIndex[] gvis = node.getGvis();
        for (int i = 0; i < summaryLists.length; i++) {
            List<TargetAndKey> targetAndKeys = summaryLists[i];
            for (TargetAndKey targetAndKey : targetAndKeys) {
                new SummaryCall(tis[i], node, targetAndKey, gvis[i], session.getLoader()).cal();
            }
        }
        if (!setIndex) {
            node.clearGvis();
        }
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
        return ng.createNodeSingleDimensionGroup(columns[deep], getters[deep], data, mergeIteratorCreators[deep], ng.getChildren());
    }

    private class MultiThreadBuilder {
        //每一层维度计算完成的数量
        private AtomicInteger[] count;
        //每一层维度被丢进线程池的数量
        private AtomicInteger[] size;

        private BIMultiThreadExecutor executor = MultiThreadManagerImpl.getInstance().getExecutorService();

        public void build() {
            count = new AtomicInteger[rowSize];
            size = new AtomicInteger[rowSize];
            for (int i = 0; i < rowSize; i++) {
                count[i] = new AtomicInteger(0);
                size[i] = new AtomicInteger(0);
            }
            SingleDimensionGroup rootGroup = root.createSingleDimensionGroup(columns[0], getters[0], null, mergeIteratorCreators[0], useRealData);
            int index = 0;
            MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
            while (result != MetricMergeResult.NULL) {
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
            if (level > rowSize - 1) {
                return false;
            }
            //执行完的迭代器的数量不等于0，并且等于上一层的丢进线程池的计算数量。
            return count[level].get() != 0 && count[level].get() == size[level].get();
        }

        private class SingleChildCal implements BISingleThreadCal {
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
                try {
                    if (level < rowSize - 1) {
                        SingleDimensionGroup rootGroup = childDimensionGroup.createSingleDimensionGroup(columns[level + 1], getters[level + 1], null, mergeIteratorCreators[level + 1], useRealData);
                        int index = 0;
                        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
                        while (result != MetricMergeResult.NULL) {
                            node.addChild(result);
                            executor.add(new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), level + 1));
                            size[level + 1].incrementAndGet();
                            index++;
                            result = rootGroup.getMetricMergeResultByWait(index);
                        }
                    }
                    multiThreadSum(node);
                } finally {
                    count[level].incrementAndGet();
                }
            }
        }

        private void multiThreadSum(MetricMergeResult node) {
            GroupValueIndex[] gvis = node.getGvis();
            for (int i = 0; i < summaryLists.length; i++) {
                List<TargetAndKey> targetAndKeys = summaryLists[i];
                for (TargetAndKey targetAndKey : targetAndKeys) {
                    executor.add(new SummaryCall(tis[i], node, targetAndKey, gvis[i], session.getLoader()));
                }
            }
            if (!setIndex) {
                node.clearGvis();
            }
        }

    }


    @Override
    public IRootDimensionGroup createClonedRoot() {
        ConstructedRootDimensionGroup root = (ConstructedRootDimensionGroup) super.createClonedRoot();
        root.rootNode = rootNode;
        root.targetSort = targetSort;
        root.calCalculators = calCalculators;
        root.filterDimension = filterDimension;
        return root;
    }

    @Override
    protected IRootDimensionGroup createNew() {
        return new ConstructedRootDimensionGroup();
    }
}
