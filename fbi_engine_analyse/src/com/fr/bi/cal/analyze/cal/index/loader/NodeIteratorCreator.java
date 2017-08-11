package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.sssecret.*;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.*;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.dimension.filter.field.DimensionTargetValueFilter;
import com.fr.bi.field.dimension.filter.general.GeneralANDDimensionFilter;
import com.fr.bi.field.filtervalue.string.nfilter.StringTOPNFilterValue;
import com.fr.bi.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.SumType;
import com.fr.bi.field.target.target.TargetType;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.sort.BISortUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/11/17.
 */
public class NodeIteratorCreator {
    private static GroupValueIndex ALL_SHOW = GVIFactory.createAllShowIndexGVI(1);
    protected BISession session;
    protected List<MetricGroupInfo> metricGroupInfoList;
    protected BIDimension[] rowDimension;
    private BISummaryTarget[] usedTargets;
    protected int sumLength;
    protected boolean isRealData;
    private NameObject targetSort;
    protected NameObject[] dimensionTargetSort;
    private TargetFilter filter;
    private List<TargetFilter> authFilter;
    private final boolean showSum;
    protected final boolean setIndex;
    protected final boolean calAllPage;
    protected BIMultiThreadExecutor executor;
    protected NodeCreator nodeCreator;
    private Map<String, BISummaryTarget> targetIdMap;

    public NodeIteratorCreator(List<MetricGroupInfo> metricGroupInfoList, BIDimension[] rowDimension, BISummaryTarget[] usedTargets, int sumLength, Map<String, DimensionFilter> targetFilterMap, boolean isRealData, BISession session, NameObject targetSort, TargetFilter filter, List<TargetFilter> authFilter, boolean showSum, boolean setIndex, boolean calAllPage, BIMultiThreadExecutor executor, NodeCreator nodeCreator) {
        this.metricGroupInfoList = metricGroupInfoList;
        this.rowDimension = rowDimension;
        this.usedTargets = usedTargets;
        this.sumLength = sumLength;
        this.isRealData = isRealData;
        this.session = session;
        this.targetSort = targetSort;
        this.filter = filter;
        this.authFilter = authFilter;
        this.showSum = showSum;
        this.setIndex = setIndex;
        this.calAllPage = calAllPage;
        this.executor = executor;
        this.nodeCreator = nodeCreator;
        initTargetIdMap();
        checkTargetSort();
        checkTargetFilterMap(targetFilterMap);
    }

    private void initTargetIdMap() {
        targetIdMap = new HashMap<String, BISummaryTarget>();
        for (BISummaryTarget target : usedTargets) {
            targetIdMap.put(target.getName(), target);
        }
    }

    private void checkTargetSort() {
        dimensionTargetSort = new NameObject[rowDimension.length];
        if (targetSort != null) {
            boolean contains = false;
            for (BISummaryTarget t : usedTargets) {
                if (ComparatorUtils.equals(t.getValue(), targetSort.getName())) {
                    contains = true;
                }
            }
            if (!contains) {
                targetSort = null;
            }
        }
        for (int i = 0; i < rowDimension.length; i++) {
            NameObject dimTargetSort = getDimTargetSort(rowDimension[i]);
            if (targetSort != null) {
                dimensionTargetSort[i] = targetSort;
            } else {
                dimensionTargetSort[i] = dimTargetSort;
            }
        }

    }

    private NameObject getDimTargetSort(BIDimension dimension) {
        String target = dimension.getSortTarget();
        if (target != null) {
            boolean contains = false;
            for (BISummaryTarget t : usedTargets) {
                if (ComparatorUtils.equals(t.getValue(), target)) {
                    contains = true;
                }
            }
            if (!contains) {
                target = null;
            }
        }
        if (target != null) {
            return new NameObject(target, dimension.getSort().getSortType());
        }
        return null;
    }

    //把格子上的指标过滤转化为最后一个维度的维度过滤
    private void checkTargetFilterMap(Map<String, DimensionFilter> targetFilterMap) {
        Map<String, DimensionFilter> filterMap = new HashMap<String, DimensionFilter>();
        for (Map.Entry<String, DimensionFilter> entry : targetFilterMap.entrySet()) {
            if (targetIdMap.containsKey(entry.getKey())) {
                filterMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (!filterMap.isEmpty() && rowDimension.length > 0) {
            DimensionFilter[] children = filterMap.values().toArray(new DimensionFilter[filterMap.size()]);
            DimensionFilter f = rowDimension[rowDimension.length - 1].getFilter();
            if (f != null) {
                DimensionFilter[] fs = new DimensionFilter[children.length + 1];
                System.arraycopy(children, 0, fs, 0, children.length);
                fs[fs.length - 1] = f;
                children = fs;
            }
            GeneralANDDimensionFilter filter = new GeneralANDDimensionFilter();
            filter.setChilds(children);
            rowDimension[rowDimension.length - 1].setFilter(filter);
        }
    }

    private CalLevel getCalLevel() {
        if (isaAllNode()) {
            return CalLevel.ALL_NODE;
        }
        return CalLevel.PART_NODE;
    }

    //如果参数要求全部计算，或者有需要全部计算的配置类计算
    private boolean isaAllNode() {
        return calAllPage || hasAllCalculateMetrics(targetIdMap.keySet());
    }

    public IRootDimensionGroup createRoot() {
        switch (getCalLevel()) {
            case ALL_NODE:
                return createAllNodeIteratorRoot();
            default:
                return createNormalIteratorRoot();
        }
    }

    private IRootDimensionGroup createNormalIteratorRoot() {
        if (usedTargets == null || usedTargets.length == 0) {
            return new NoneMetricRootDimensionGroup(metricGroupInfoList, createNoneMetricMergeIteratorCreator(), nodeCreator,  sumLength, session, isRealData, filter, authFilter, getDirectDimensionFilter());
        }
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null && directFilterIndexes[i] != ALL_SHOW) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(directFilterIndexes[i]));
            }
        }
        RootDimensionGroup rootDimensionGroup = new RootDimensionGroup(metricGroupInfoList, createNormalMergeIteratorCreator(), nodeCreator,  sumLength, session, isRealData);
        GroupValueIndex[] inDirectFilterIndexes = getInDirectFilterIndex(rootDimensionGroup.getRoot(), rootDimensionGroup.getGetters(), rootDimensionGroup.getColumns());
        if (inDirectFilterIndexes.length != 0) {
            GroupValueIndex[] gvis = rootDimensionGroup.getRoot().getGvis();
            for (int i = 0; i < inDirectFilterIndexes.length; i++) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(inDirectFilterIndexes[i]));
                gvis[i] = metricGroupInfoList.get(i).getFilterIndex();
            }
            rootDimensionGroup.getRoot().setGvis(gvis);
        }
        return rootDimensionGroup;
    }

    //没有指标的情况的MergeIteratorCreator, 肯定没有指标排序, 只需要考虑过滤的情况
    //也肯定用不着多线程
    private MergeIteratorCreator[] createNoneMetricMergeIteratorCreator() {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            createFilterIteratorCreator(null, mergeIteratorCreators, i, rowDimension[i].getFilter(), null);
        }
        return mergeIteratorCreators;
    }

    //分页计算的MergeIteratorCreator, 由于维度过滤都放到了inDirectFilterIndexes里面处理, 这里去掉维度过滤
    private MergeIteratorCreator[] createNormalMergeIteratorCreator() {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        boolean hasSingleNodeCalMetrics = hasSingleNodeCalMetrics();
        boolean calIndirectFilter = shouldCalIndirectDimensionFilterGVI();
        for (int i = 0; i < rowDimension.length; i++) {
            //只有最后一层才算配置类计算
            boolean calSingleNodeMetrics = hasSingleNodeCalMetrics && i == rowDimension.length - 1;
            if (calAllNode(calIndirectFilter, rowDimension[i].getFilter(), i, calSingleNodeMetrics)) {
                createAllNodeCreator(mergeIteratorCreators, i, calIndirectFilter ? null : rowDimension[i].getFilter(), dimensionTargetSort[i], new SimpleMergeIteratorCreator(), executor, calSingleNodeMetrics);
            } else {
                mergeIteratorCreators[i] = new SimpleMergeIteratorCreator();
            }
        }
        return mergeIteratorCreators;
    }

    private boolean calAllNode(boolean calIndirectFilter, DimensionFilter filter, int i, boolean calSingleNodeMetrics) {
        //没有计算不能转化为索引的维度过滤，或者有指标排序，或者计算需要整个节点的配置类计算，就计算单个节点
        return (!calIndirectFilter && filter != null && !filter.canCreateDirectFilter()) || BISortUtils.hasTargetSort(dimensionTargetSort[i]) || calSingleNodeMetrics;
    }

    //IndirectFilter的MergeIteratorCreator, 不需要处理排序, 同时, 只有第一个维度可以多线程计算, 如果第二个维度用多线程, 当构建当前节点的任务跟计算当前子节点的任务排在一个队列时会死锁
    private MergeIteratorCreator[] createIndirectFilterMergeIteratorCreator() {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            DimensionFilter filter = rowDimension[i].getFilter();
            if (filter == null || filter.canCreateDirectFilter()) {
                mergeIteratorCreators[i] = new SimpleMergeIteratorCreator();
            } else {
                createFilterIteratorCreator(null, mergeIteratorCreators, i, filter, null);
            }
        }
        return mergeIteratorCreators;
    }

    //把简单的过滤条件提取出来
    private void createFilterIteratorCreator(BIMultiThreadExecutor executor, MergeIteratorCreator[] mergeIteratorCreators, int i, DimensionFilter filter, NameObject targetSort) {
        if (filter == null || filter.canCreateDirectFilter()) {
            mergeIteratorCreators[i] = new SimpleMergeIteratorCreator();
        } else if (filter instanceof DimensionTargetValueFilter) {
            FilterValue filterValue = ((DimensionTargetValueFilter) filter).getFilterValue();
            if (filterValue instanceof StringTOPNFilterValue) {
                mergeIteratorCreators[i] = new NFilterMergeIteratorCreator(((StringTOPNFilterValue) filterValue).getN());
            } else if (filterValue instanceof StringOneValueFilterValue) {
                mergeIteratorCreators[i] = new FilterMergeIteratorCreator((StringOneValueFilterValue) filterValue);
            } else {
                createAllNodeCreator(mergeIteratorCreators, i, filter, targetSort, new SimpleMergeIteratorCreator(), executor, false);
            }
        } else {
            createAllNodeCreator(mergeIteratorCreators, i, filter, targetSort, new SimpleMergeIteratorCreator(), executor, false);
        }
    }

    //需要全部计算子节点的IteratorCreator
    private void createAllNodeCreator(MergeIteratorCreator[] mergeIteratorCreators, int index, DimensionFilter filter, NameObject targetSort, MergeIteratorCreator creator, BIMultiThreadExecutor executor, boolean calSingleNodeMetrics) {
        List<TargetAndKey>[] metricsToCalculate = new List[metricGroupInfoList.size()];
        Map<String, TargetCalculator> calculatedMap = new HashMap<String, TargetCalculator>();
        Set<String> metrics = new HashSet<String>();
        List<String> usedTargets = new ArrayList<String>();
        boolean hasAllConfigureMetricsFilter = false;
        if (filter != null) {
            usedTargets = filter.getUsedTargets();
            hasAllConfigureMetricsFilter = hasAllCalculateMetrics(usedTargets);
        }
        if (targetSort != null) {
            usedTargets.add(targetSort.getName());
        }
        if (calSingleNodeMetrics) {
            for (BISummaryTarget target : this.usedTargets) {
                if (target.calculateSingleNode(this.usedTargets)) {
                    usedTargets.add(target.getName());
                }
            }
        }
        for (String id : usedTargets) {
            getRelatedNormalIds(id, metrics);
        }
        List<CalCalculator> calCalculators = getRelatedCalMetric(usedTargets);
        fillCalculatedMap(calculatedMap, calCalculators);
        fillMetricsToCalculate(metrics, metricsToCalculate, calculatedMap);
        createAllNodeCreator(mergeIteratorCreators, index, filter, targetSort, creator, executor, metricsToCalculate, calculatedMap, hasAllConfigureMetricsFilter, calCalculators);
    }

    protected void createAllNodeCreator(MergeIteratorCreator[] mergeIteratorCreators, int index, DimensionFilter filter, NameObject targetSort, MergeIteratorCreator creator, BIMultiThreadExecutor executor, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap, boolean hasAllConfigureMetricsFilter, List<CalCalculator> calCalculators) {
        //如果不过滤配置类计算指标, 就直接过滤, 过滤是配置类计算需要等到全部计算完了才过滤
        mergeIteratorCreators[index] = new AllNodeMergeIteratorCreator(hasAllConfigureMetricsFilter ? null : filter, targetSort, metricsToCalculate, calculatedMap, creator, executor, calCalculators);
    }

    //是否有需要计算整个节点的配置类计算（一定是组内配置类计算，并且需要计算单个node才能计算出结果，目前只有组内所有值求和不需要）
    private boolean hasSingleNodeCalMetrics() {
        for (BISummaryTarget target : usedTargets) {
            if (target != null && target.calculateSingleNode(usedTargets)) {
                return true;
            }
        }
        return false;
    }

    //如果有非组内的配置类计算，则需要全部计算
    private boolean hasAllCalculateMetrics(Collection<String> usedTargets) {
        for (String id : usedTargets) {
            BISummaryTarget target = targetIdMap.get(id);
            if (target != null && target.calculateAllNode()) {
                return true;
            }
        }

        return false;
    }

    //获取相关的基本指标（非计算指标）

    private void getRelatedNormalIds(String name, Set<String> ids) {
        BISummaryTarget target = targetIdMap.get(name);
        if (target == null) {
            return;
        }
        if (target.getType() != TargetType.NORMAL) {
            Collection<String> usedTargets = target.getCalculateUseTargetIDs();
            if (usedTargets != null) {
                for (String id : usedTargets) {
                    if (!ComparatorUtils.equals(id, target.getName())) {
                        getRelatedNormalIds(id, ids);
                    }
                }
            }
        } else {
            ids.add(target.getName());
        }
    }

    //获取相关的计算指标
    private List<CalCalculator> getRelatedCalMetric(Collection<String> ids) {
        Set<String> calIds = new HashSet<String>();
        for (String id : ids) {
            getRelatedCalMetricIds(calIds, id);
        }
        List<CalCalculator> formulaCalculator = new ArrayList<CalCalculator>();
        for (String id : calIds) {
            formulaCalculator.add((CalCalculator) targetIdMap.get(id).createSummaryCalculator());
        }
        return formulaCalculator;
    }

    private void getRelatedCalMetricIds(Set<String> calIds, String id) {
        BISummaryTarget target = targetIdMap.get(id);
        if (target == null) {
            return;
        }
        if (target.getType() != TargetType.NORMAL) {
            calIds.add(target.getName());
        }
        Collection<String> usedTargets = target.getCalculateUseTargetIDs();
        if (usedTargets != null) {
            for (String usedTarget : usedTargets) {
                if (!ComparatorUtils.equals(usedTarget, target.getName())) {
                    getRelatedCalMetricIds(calIds, usedTarget);
                }
            }
        }
    }


    private void fillCalculatedMap(Map<String, TargetCalculator> calculatedMap, List<CalCalculator> formulaCalculators) {
        for (CalCalculator calCalculator : formulaCalculators) {
            calculatedMap.put(calCalculator.getName(), calCalculator);
        }
    }

    private void fillMetricsToCalculate(Set<String> metrics, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap) {
        for (int i = 0; i < metricGroupInfoList.size(); i++) {
            MetricGroupInfo info = metricGroupInfoList.get(i);
            List<TargetAndKey> calList = new ArrayList<TargetAndKey>();
            metricsToCalculate[i] = calList;
            List<TargetAndKey> list = info.getSummaryList();
            for (TargetAndKey targetAndKey : list) {
                if (metrics.contains(targetAndKey.getTargetId())) {
                    calculatedMap.put(targetAndKey.getTargetId(), targetAndKey.getCalculator());
                    calList.add(targetAndKey);
                }
            }
        }
    }

    private ConstructedRootDimensionGroup createAllNodeIteratorRoot() {
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null && directFilterIndexes[i] != ALL_SHOW) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(directFilterIndexes[i]));
            }
        }
        //是否提前计算了维度过滤
        //如果有配置类计算的过滤，则需要构建完node之后再计算，之前的过滤一个都别动，要不然影响配置类计算的。
        boolean canPreFilter = hasDimensionInDirectFilter() && !hasAllNodeIndirectDimensionFilter();
        ConstructedRootDimensionGroup constructedRootDimensionGroup = createConstructedRootDimensionGroup(canPreFilter);
        //如果没有配置类计算的过滤，并且最后一个维度没有过滤，可以先算一下IndirectFilter
        if (canPreFilter) {
            GroupValueIndex[] inDirectFilterIndexes = getInDirectFilterIndex(constructedRootDimensionGroup.getRoot(), constructedRootDimensionGroup.getGetters(), constructedRootDimensionGroup.getColumns());
            if (inDirectFilterIndexes.length != 0) {
                GroupValueIndex[] gvis = constructedRootDimensionGroup.getRoot().getGvis();
                for (int i = 0; i < inDirectFilterIndexes.length; i++) {
                    metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(inDirectFilterIndexes[i]));
                    gvis[i] = metricGroupInfoList.get(i).getFilterIndex();
                }
                constructedRootDimensionGroup.getRoot().setGvis(gvis);
            }
        }
        constructedRootDimensionGroup.construct();
        return constructedRootDimensionGroup;
    }

    protected ConstructedRootDimensionGroup createConstructedRootDimensionGroup(boolean canPreFilter) {
        return new ConstructedRootDimensionGroup(metricGroupInfoList, createAllNodeMergeIteratorCreator(), sumLength, session, isRealData, dimensionTargetSort, getCalCalculators(), canPreFilter || !hasDimensionInDirectFilter() ? null : rowDimension, setIndex, hasInSumMetric(), executor, nodeCreator, calAllPage);
    }

    /**
     * 这边不需要排序跟过滤
     * 在全部计算完之后会再排
     * 如果有配置类计算的过滤，则需要全部计算完之后再过滤，如果没有，则会被提取
     *
     * @return
     */
    protected MergeIteratorCreator[] createAllNodeMergeIteratorCreator() {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            mergeIteratorCreators[i] = new SimpleMergeIteratorCreator();
        }
        return mergeIteratorCreators;
    }


    public List<CalCalculator> getCalCalculators() {
        List<CalCalculator> calCalculators = new ArrayList<CalCalculator>();
        for (BISummaryTarget target : usedTargets) {
            if (target.getType() != TargetType.NORMAL) {
                calCalculators.add((CalCalculator) target.createSummaryCalculator());
            }
        }
        return calCalculators;
    }

    protected boolean hasInSumMetric() {
        for (BISummaryTarget target : usedTargets) {
            if (hasInSumMetric(target)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasInSumMetric(BISummaryTarget target) {
        return target != null && (target.getType() == TargetType.NORMAL && target.getSumType() == SumType.GVI);
    }


    //维度过滤中包含需要计算整个node的配置配计算
    private boolean hasAllNodeIndirectDimensionFilter() {
        for (BIDimension dimension : rowDimension) {
            DimensionFilter filter = dimension.getFilter();
            if (filter != null && !filter.canCreateDirectFilter()) {
                for (String id : filter.getUsedTargets()) {
                    BISummaryTarget target = targetIdMap.get(id);
                    if (target != null && target.calculateAllNode()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private int getLastIndirectFilterDimensionIndex() {
        int index = -1;
        for (int i = 0; i < rowDimension.length; i++) {
            if (rowDimension[i].getFilter() != null && !rowDimension[i].getFilter().canCreateDirectFilter()) {
                index = i;
            }
        }
        return index;
    }

    private GroupValueIndex[] createDirectFilterIndex() {
        GroupValueIndex[] retIndexes = new GroupValueIndex[metricGroupInfoList.size()];
        Arrays.fill(retIndexes, ALL_SHOW);
        for (int i = 0; i < retIndexes.length; i++) {
            for (int deep = 0; deep < rowDimension.length; deep++) {
                DimensionFilter resultFilter = rowDimension[deep].getFilter();
                if (resultFilter != null && resultFilter.canCreateDirectFilter()) {
                    DimensionCalculator c = metricGroupInfoList.get(i).getRows()[deep];
                    BusinessTable t = metricGroupInfoList.get(i).getMetric();
                    if (t.getTableSource() != null) {
                        GroupValueIndex filterIndex = resultFilter.createFilterIndex(c, t, session.getLoader(), session.getUserId());
                        if (filterIndex != null) {
                            retIndexes[i] = retIndexes[i].and(filterIndex);
                        } else {
                            retIndexes[i] = null;
                        }
                    }

                }
            }
        }
        return retIndexes;
    }

    private GroupValueIndex[] getInDirectFilterIndex(NoneDimensionGroup root, ICubeValueEntryGetter[][] getters, DimensionCalculator[][] columns) {
        if (shouldCalIndirectDimensionFilterGVI()) {
            return new NodeIndirectFilterIndexCalculator(root, getters, columns, createIndirectFilterMergeIteratorCreator(), isRealData, getLastIndirectFilterDimensionIndex(), executor).cal();
        }
        return new GroupValueIndex[0];
    }

    //是否需要计算不能直接转化为索引的维度过滤, 如果需要, 就通过构建node的方式转化为索引
    private boolean shouldCalIndirectDimensionFilterGVI() {
        return hasDimensionInDirectFilter() && (showSum || hasTargetSortBeforeIndex(getLastIndirectFilterDimensionIndex()));
    }

    private DimensionFilter[] getDirectDimensionFilter() {
        DimensionFilter[] filters = new DimensionFilter[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            DimensionFilter resultFilter = rowDimension[i].getFilter();
            if (resultFilter != null && resultFilter.canCreateDirectFilter()) {
                filters[i] = resultFilter;
            }
        }
        return filters;
    }

    protected boolean hasDimensionInDirectFilter() {
        for (BIDimension dimension : rowDimension) {
            DimensionFilter filter = dimension.getFilter();
            if (filter != null && !filter.canCreateDirectFilter()) {
                return true;
            }
        }
        return false;
    }

    //从第几个维度之前有没有指标排序
    private boolean hasTargetSortBeforeIndex(int index) {
        for (int i = 0; i < index; i++) {
            if (BISortUtils.hasTargetSort(dimensionTargetSort[i])) {
                return true;
            }
        }
        return false;
    }
}
