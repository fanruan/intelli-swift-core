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
        SingleDimensionGroup rootGroup =  root.createSingleDimensionGroup(columns[0], getters[0], null, mergeIteratorCreators[0], useRealData);
        int index = 0;
        MetricMergeResult result = rootGroup.getMetricMergeResultByWait(index);
        BIMultiThreadExecutor executor = null;
        if (MultiThreadManagerImpl.getInstance().isMultiCall()){
            executor = MultiThreadManagerImpl.getInstance().getExecutorService();
        }
        while (result != MetricMergeResult.NULL){
            rootNode.addChild(result);
            SingleChildCal cal = new SingleChildCal(result, rootGroup.getChildDimensionGroup(index), 1);
            if (executor != null){
                executor.add(cal);
            } else {
                cal.cal();
            }
            index++;
            result = rootGroup.getMetricMergeResultByWait(index);
        }
        if (executor != null){
            MultiThreadManagerImpl.getInstance().awaitExecutor(session);
        }
        sum(rootNode);
        sumCalculateMetrics();
        root.setChildren(rootNode.getChilds());
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
                if (level < lastConstructedDimensionIndex){
                    cal(result, rootGroup.getChildDimensionGroup(index), level + 1);
                }
                index++;
                result = rootGroup.getMetricMergeResultByWait(index);
            }
            sum(node);
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
