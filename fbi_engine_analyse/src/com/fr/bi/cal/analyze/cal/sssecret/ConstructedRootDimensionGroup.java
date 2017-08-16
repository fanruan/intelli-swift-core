package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.multithread.BISingleThreadCal;
import com.fr.bi.cal.analyze.cal.multithread.SummaryCal;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.result.NodeUtils;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.sort.BISortUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2017/1/17.
 */
public class ConstructedRootDimensionGroup extends RootDimensionGroup {

    protected MetricMergeResult rootNode;

    private NameObject[] dimensionTargetSort;

    private List<CalCalculator> calCalculators;

    private BIDimension[] filterDimension;

    protected boolean setIndex;

    private boolean hasInSumMetric;

    protected BIMultiThreadExecutor executor;

    private boolean calAllPage;

    private int[] sortType;

    private TargetGettingKey[] sortTargetKey;

    public ConstructedRootDimensionGroup() {

    }

    public ConstructedRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, int sumLength, BISession session, boolean useRealData,
                                         NameObject[] dimensionTargetSort, List<CalCalculator> calCalculators, BIDimension[] filterDimension, boolean setIndex, boolean hasInSumMetric, BIMultiThreadExecutor executor, NodeCreator nodeCreator, boolean calAllPage) {

        super(metricGroupInfoList, mergeIteratorCreators, nodeCreator, sumLength, session, useRealData);
        this.calCalculators = calCalculators;
        this.filterDimension = filterDimension;
        //需要返回带索引的node或者需要使用gvisum的情况
        this.setIndex = setIndex || (hasInSumMetric && filterDimension != null);
        this.dimensionTargetSort = dimensionTargetSort;
        this.hasInSumMetric = hasInSumMetric;
        this.executor = executor;
        this.calAllPage = calAllPage;
    }

    public Node getConstructedRoot() {

        return rootNode;
    }

    public void construct() {

        initSort();
        initRootNode();
    }

    private void initSort() {

        sortType = new int[dimensionTargetSort.length];
        sortTargetKey = new TargetGettingKey[dimensionTargetSort.length];
        for (int i = 0; i < dimensionTargetSort.length; i++) {
            NameObject targetSort = dimensionTargetSort[i];
            if (BISortUtils.hasTargetSort(targetSort)) {
                sortType[i] = (Integer) targetSort.getObject();
                boolean find = false;
                for (int j = 0; j < metricGroupInfoList.size(); j++) {
                    MetricGroupInfo info = metricGroupInfoList.get(j);
                    List<TargetAndKey> list = info.getSummaryList();
                    for (TargetAndKey targetAndKey : list) {
                        if (ComparatorUtils.equals(targetAndKey.getTargetId(), targetSort.getName())) {
                            sortTargetKey[i] = targetAndKey.getTargetGettingKey();
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        break;
                    }
                }
                //再到计算指标里面找下
                if (sortTargetKey[i] == null) {
                    for (CalCalculator calCalculator : calCalculators) {
                        if (ComparatorUtils.equals(calCalculator.createTargetGettingKey().getTargetName(), targetSort.getName())) {
                            sortTargetKey[i] = calCalculator.createTargetGettingKey();
                        }
                    }
                }
            }
        }
    }

    private void initRootNode() {

        rootNode = createNode(null, sumLength, root.getGvis());
        if (executor != null) {
            multiThreadBuild();
        } else {
            singleThreadBuild();
        }
        //只计算了子节点的情况要加一下
        if (!hasInSumMetric && rowSize > 0){
            List<TargetAndKey> keys = new ArrayList<TargetAndKey>();
            for (List<TargetAndKey> list : summaryLists) {
                for (TargetAndKey key : list) {
                    keys.addAll(nodeCreator.createTargetAndKeyList(key));
                }
            }

            NodeSummarizing summarizing = new NodeSummarizing(rootNode, keys.toArray(new TargetAndKey[keys.size()]));
            summarizing.sum();
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
        if (filterDimension != null) {
            filter(rootNode, 0, calculatorMap);
            reSum();
            sumCalculateMetrics();
        }
        if (hasTargetSort()) {
            sort(rootNode, 0);
            NodeUtils.setSibling(rootNode);
            NodeUtils.setSiblingBetweenFirstAndLastChild(rootNode);
        }
        root.setChildren(rootNode.getChilds());
        root.setMergeResult(rootNode);
        if (setIndex) {
            root.setGvis(rootNode.getGvis());
            for (int i = 0; i < metricGroupInfoList.size(); i++) {
                metricGroupInfoList.get(i).setFilterIndex(root.getGvis()[i]);
            }
        }
    }

    protected MetricMergeResult createNode(Object data, int sumLen, GroupValueIndex[] gvis){
        return nodeCreator.createMetricMergeResult(data, sumLen, gvis);
    }


    protected MetricMergeResult convertNode(MetricMergeResult node){
        return nodeCreator.convertMetricMergeResult(node);
    }

    private boolean hasTargetSort() {

        for (NameObject object : dimensionTargetSort) {
            if (BISortUtils.hasTargetSort(object)) {
                return true;
            }
        }
        return false;
    }

    private void clearEmptyNode(Node node) {

        Node parent = node.getParent();
        if (parent != null) {
            List<Node> children = parent.getChilds();
            parent.clearChildren();
            for (Node child : children) {
                if (child != node) {
                    parent.addChild(child);
                }
            }
            if (parent.getChildLength() == 0) {
                clearEmptyNode(parent);
            }
        }
    }

    private void filter(MetricMergeResult node, int deep, Map<String, TargetCalculator> calculatorMap) {

        if (deep < rowSize) {
            DimensionFilter filter = filterDimension == null ? null : filterDimension[deep].getFilter();
            if (filter != null) {
                List<Node> children = filter(node.getChilds(), deep, calculatorMap);
                node.clearChildren();
                if (children == null || children.isEmpty()) {
                    clearEmptyNode(node);
                } else {
                    for (Node n : children) {
                        node.addChild(n);
                        if (setIndex) {
                            // node 的gvi需要更新
                            GroupValueIndex c[] = new GroupValueIndex[node.getGvis().length];
                            int i = 0;
                            for (GroupValueIndex g : ((MetricMergeResult) n).getGvis()) {
                                c[i] = GVIUtils.OR(c[i++], g);
                            }
                            node.setGvis(c);
                        }
                    }
                }
            }
            GroupValueIndex c[] = null;
            if (setIndex) {
                c = new GroupValueIndex[node.getGvis().length];
            }
            for (Node n : node.getChilds()) {
                filter((MetricMergeResult) n, deep + 1, calculatorMap);
                if (setIndex) {
                    int i = 0;
                    for (GroupValueIndex g : ((MetricMergeResult) n).getGvis()) {
                        c[i] = GVIUtils.OR(c[i++], g);
                    }
                }
            }
            if (setIndex) {
                node.setGvis(c);
            }
        }
    }

    private List<Node> filter(List<Node> children, final int deep, Map<String, TargetCalculator> calculatorMap) {

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
        return results;
    }

    //有过滤要重新汇总下
    private void reSum() {

        List<TargetAndKey> keys = new ArrayList<TargetAndKey>();
        for (List<TargetAndKey> list : summaryLists) {
            for (TargetAndKey key : list) {
                keys.addAll(nodeCreator.createTargetAndKeyList(key));
            }
        }
        NodeSummarizing summarizing = hasInSumMetric ? new NodeGVISummarizing(rootNode, keys.toArray(new TargetAndKey[keys.size()])) : new NodeSummarizing(rootNode,  keys.toArray(new TargetAndKey[keys.size()]));
        summarizing.sum();
        //gvi汇总之后如果需要用到全部结果，或者需要排序，就对node再汇总一次，最后一层不需要汇总
        if (hasInSumMetric) {
            if (calAllPage || hasTargetSort()) {
                sumAfterGVISummarizing(rootNode, 0);
            }
        }
    }

    private void sumAfterGVISummarizing(MetricMergeResult node, int deep) {

        sum(node, -1);
        //最后一层不需要resum
        if (deep < rowSize - 1) {
            for (Node n : node.getChilds()) {
                sumAfterGVISummarizing((MetricMergeResult) n, deep + 1);
            }
        }
    }


    private void sort(MetricMergeResult node, int deep) {

        if (deep < rowSize) {
            if (BISortUtils.hasTargetSort(dimensionTargetSort[deep])) {
                final int fDeep = deep;
                Collections.sort(node.getChilds(), new Comparator<Node>() {

                    @Override
                    public int compare(Node o1, Node o2) {

                        Number v1 = o1.getSummaryValue(sortTargetKey[fDeep]);
                        Number v2 = o2.getSummaryValue(sortTargetKey[fDeep]);
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
                        return (sortType[fDeep] == BIReportConstant.SORT.NUMBER_ASC || sortType[fDeep] == BIReportConstant.SORT.ASC) == v ? -1 : 1;
                    }
                });
            }
            for (Node n : node.getChilds()) {
                sort((MetricMergeResult) n, deep + 1);
                n.setSibling(null);
            }
        }
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
            result = convertNode(result);
            node.addChild(result);
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

        new MultiThreadBuilder().build();
    }

    private void sumCalculateMetrics() {

        NodeUtils.setSiblingBetweenFirstAndLastChild(rootNode);
        if (!calCalculators.isEmpty()) {
            List<TargetCalculator> calculatorList = new ArrayList<TargetCalculator>();
            for (MetricGroupInfo info : metricGroupInfoList) {
                for (TargetAndKey key : info.getSummaryList()) {
                    calculatorList.add(key.getCalculator());
                }
            }
            List<CalCalculator> calCalculators = new ArrayList<CalCalculator>();
            calCalculators.addAll(this.calCalculators);
            nodeCreator.sumCalculateMetrics(calculatorList, calCalculators, rootNode);
        }
    }

    protected void sum(MetricMergeResult node, int deep) {
        if (hasInSumMetric || deep == rowSize - 1){
            GroupValueIndex[] gvis = node.getGvis();
            for (int i = 0; i < summaryLists.length; i++) {
                List<TargetAndKey> targetAndKeys = summaryLists[i];
                for (TargetAndKey targetAndKey : targetAndKeys) {
                    for (TargetAndKey key : nodeCreator.createTargetAndKeyList(targetAndKey)){
                        new SummaryCal(tis[i], node, key, gvis[i], session.getLoader()).cal();
                    }
                }
            }
        }
        if (!setIndex) {
            node.clearGvis();
        }
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {

        return ng.createNodeSingleDimensionGroup(columns[deep], getters[deep], data, mergeIteratorCreators[deep], ng.getChildren());
    }

    protected class MultiThreadBuilder {

        //每一层维度计算完成的数量
        private AtomicInteger[] count;

        //每一层维度被丢进线程池的数量
        private AtomicInteger[] size;

        public void build() {
            //数组的最后一个来表示汇总的线程是否完成
            count = new AtomicInteger[rowSize + 1];
            size = new AtomicInteger[rowSize + 1];
            for (int i = 0; i < count.length; i++) {
                count[i] = new AtomicInteger(0);
                size[i] = new AtomicInteger(0);
            }
            if (rowSize > 0) {
                SingleDimensionGroup rootGroup = root.createSingleDimensionGroup(columns[0], getters[0], null, mergeIteratorCreators[0], useRealData);
                int index = 0;
                MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
                while (result != MetricMergeResult.NULL) {
                    result = convertNode(result);
                    rootNode.addChild(result);
                    size[0].incrementAndGet();
                    addTask(new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), 0));
                    index++;
                    result = rootGroup.getMetricMergeResultByWait(index);
                }
            }
            //如果多线程计算没有结束，就等结束
            multiThreadSum(rootNode, -1);
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
        }

        protected void addTask(SingleChildCal cal) {
            executor.add(cal);
        }

        /**
         * 计算是否全部完成。
         * 乍一看，完成的等于添加了，就是全完成了，没问题。
         * 后来再想想，不对呀，万一计算线程太快，加一个算一个，到某一个时间节点刚好count跟size都相等了，岂不是还没算完就结束了？赶紧判断下是不是每一层都加进来了。
         * 最后想明白了，是不会出现没算完就结束的。因为再主线程里面已经把size[0]给丢满了，如果count[i]==size[i]的情况，size[i+1]一定是满的，
         * 而multiThreadSum是发生再count[level].incrementAndGet()之前的，所以size与count相等说明一定是计算完了
         *
         * @return
         */
        private boolean allCompleted() {

            for (int i = 0; i < count.length; i++) {
                if (count[i].get() != size[i].get()) {
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

            if (level > rowSize) {
                return false;
            }
            //执行完的迭代器的数量不等于0，并且等于上一层的丢进线程池的计算数量。
            return count[level].get() != 0 && count[level].get() == size[level].get();
        }

        protected class SingleChildCal implements BISingleThreadCal {

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

                try {
                    if (level < rowSize - 1) {
                        SingleDimensionGroup rootGroup = childDimensionGroup.createSingleDimensionGroup(columns[level + 1], getters[level + 1], null, mergeIteratorCreators[level + 1], useRealData);
                        int index = 0;
                        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
                        while (result != MetricMergeResult.NULL) {
                            result = convertNode(result);
                            node.addChild(result);
                            addTask(new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), level + 1));
                            size[level + 1].incrementAndGet();
                            index++;
                            result = rootGroup.getMetricMergeResultByWait(index);
                        }
                    }
                    multiThreadSum(node ,level);
                } finally {
                    count[level].incrementAndGet();
                    checkComplete(level);
                }
            }
        }

        private class SingleSummaryCal extends SummaryCal {

            public SingleSummaryCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader) {

                super(ti, node, targetAndKey, gvi, loader);
            }

            @Override
            public void cal() {
                try{
                    super.cal();
                } finally {
                    count[rowSize].incrementAndGet();
                    checkComplete(rowSize);
                }
            }
        }

        protected void multiThreadSum(MetricMergeResult node, int deep) {
            //不能直接相加的汇总或者是子节点才需要汇总
            if (hasInSumMetric || deep == rowSize - 1){
                GroupValueIndex[] gvis = node.getGvis();
                for (int i = 0; i < summaryLists.length; i++) {
                    List<TargetAndKey> targetAndKeys = summaryLists[i];
                    for (TargetAndKey targetAndKey : targetAndKeys) {
                        for (TargetAndKey key : nodeCreator.createTargetAndKeyList(targetAndKey)){
                            size[rowSize].incrementAndGet();
                            executor.add(new SingleSummaryCal(tis[i], node, key, gvis[i], session.getLoader()));
                        }
                    }
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
        root.dimensionTargetSort = dimensionTargetSort;
        root.calCalculators = calCalculators;
        root.filterDimension = filterDimension;
        root.nodeCreator = nodeCreator;
        return root;
    }

    @Override
    public void checkMetricGroupInfo(NodeCreator creator, List<MetricGroupInfo> metricGroupInfoList) {
        this.nodeCreator = creator;
        if (!ComparatorUtils.equals(this.metricGroupInfoList, metricGroupInfoList)){
            super.checkMetricGroupInfo(creator, metricGroupInfoList);
            construct();
        }
    }

    @Override
    protected IRootDimensionGroup createNew() {

        return new ConstructedRootDimensionGroup();
    }
}
