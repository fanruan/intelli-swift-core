package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NoneMetricRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.PartConstructedRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.RootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.*;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.dimension.filter.field.DimensionTargetValueFilter;
import com.fr.bi.field.filtervalue.string.nfilter.StringTOPNFilterValue;
import com.fr.bi.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.TargetType;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/11/17.
 */
public class NodeIteratorCreator {
    private static GroupValueIndex ALL_SHOW = GVIFactory.createAllShowIndexGVI(1);
    private BISession session;
    private List<MetricGroupInfo> metricGroupInfoList = new ArrayList<MetricGroupInfo>();
    private BIDimension[] rowDimension;
    private BISummaryTarget[] usedTargets;
    private Map<String, DimensionFilter> targetFilterMap;
    private boolean isRealData;
    private NameObject targetSort;
    private TargetFilter filter;
    private final boolean showSum;
    private final boolean setIndex;
    private final boolean calAllPage;
    private List<CalCalculator> configureRelatedCalculators;
    private Map<String, BISummaryTarget> targetIdMap;

    public NodeIteratorCreator(List<MetricGroupInfo> metricGroupInfoList, BIDimension[] rowDimension, BISummaryTarget[] usedTargets, Map<String, DimensionFilter> targetFilterMap, boolean isRealData, BISession session, NameObject targetSort, TargetFilter filter, boolean showSum, boolean setIndex, boolean calAllPage) {
        this.metricGroupInfoList = metricGroupInfoList;
        this.rowDimension = rowDimension;
        this.usedTargets = usedTargets;
        this.targetFilterMap = targetFilterMap;
        this.isRealData = isRealData;
        this.session = session;
        this.targetSort = targetSort;
        this.filter = filter;
        this.showSum = showSum;
        this.setIndex = setIndex;
        this.calAllPage = calAllPage;
        checkTargetSort();
        classifyMetrics();
    }

    private void classifyMetrics() {
        targetIdMap = new HashMap<String, BISummaryTarget>();
        for (BISummaryTarget target : usedTargets) {
            targetIdMap.put(target.getName(), target);
        }
        configureRelatedCalculators = new ArrayList<CalCalculator>();
        Set<String> relatedFormulaMetricIds = new HashSet<String>();
        for (BISummaryTarget target : usedTargets) {
            if (target.getType() == TargetType.CONFIGURE) {
                configureRelatedCalculators.add((CalCalculator) target.createSummaryCalculator());
                getRelatedFormulaMetricIds(target.getName(), relatedFormulaMetricIds);
            }
        }
        for (String id : relatedFormulaMetricIds) {
            configureRelatedCalculators.add((CalCalculator) targetIdMap.get(id).createSummaryCalculator());
        }

    }

    //获取配置类计算相关的计算指标
    private void getRelatedFormulaMetricIds(String name, Set<String> ids) {
        BISummaryTarget target = targetIdMap.get(name);
        if (target.getType() != TargetType.NORMAL) {
            if (target.getType() == TargetType.FORMULA) {
                ids.add(target.getName());
            }
            Map<String, TargetGettingKey> usedTargets = target.getTargetMap();
            if (usedTargets != null) {
                for (String id : usedTargets.keySet()) {
                    if (!ComparatorUtils.equals(id, target.getName())){
                        getRelatedFormulaMetricIds(id, ids);
                    }
                }
            }
        }
    }

    //获取相关的基本指标
    private void getRelatedNormalIds(String name, Set<String> ids) {
        BISummaryTarget target = targetIdMap.get(name);
        if (target == null){
            return;
        }
        if (target.getType() != TargetType.NORMAL) {
            Map<String, TargetGettingKey> usedTargets = target.getTargetMap();
            if (usedTargets != null) {
                for (String id : usedTargets.keySet()) {
                    if (!ComparatorUtils.equals(id, target.getName())){
                        getRelatedNormalIds(id, ids);
                    }
                }
            }
        } else {
            ids.add(target.getName());
        }
    }

    private void checkTargetSort() {
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

    }

    public CalLevel getCalLevel() {
        if (calAllPage || !configureRelatedCalculators.isEmpty()) {
            return CalLevel.ALL_NODE;
        }
        if (hasDimensionInDirectFilter() && (showSum || hasTargetSort())) {
            return CalLevel.ALL_NODE;
        }
        return CalLevel.PART_NODE;
    }

    public IRootDimensionGroup createRoot() {
        switch (getCalLevel()) {
            case ALL_NODE:
                return createAllNodeIteratorRoot();
            default:
                return createNormalIteratorRoot();
        }
    }

    /**
     * 小于index的targetSort都不算
     *
     * @param sortIndex
     * @return
     */
    private MergeIteratorCreator[] createMergeIteratorCreator(int sortIndex) {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            DimensionFilter filter = rowDimension[i].getFilter();
            MergeIteratorCreator creator = null;
            if (filter == null || filter.canCreateDirectFilter()) {
                creator = new SimpleMergeIteratorCreator();
            } else if (filter instanceof DimensionTargetValueFilter) {
                FilterValue filterValue = ((DimensionTargetValueFilter) filter).getFilterValue();
                if (filterValue instanceof StringTOPNFilterValue) {
                    creator = new NFilterMergeIteratorCreator(((StringTOPNFilterValue) filterValue).getN());
                } else if (filterValue instanceof StringOneValueFilterValue) {
                    creator = new FilterMergeIteratorCreator((StringOneValueFilterValue) filterValue);
                }
            } else {
                creator = new SimpleMergeIteratorCreator();
            }
            createAllNodeCreator(mergeIteratorCreators, i, filter, i >= sortIndex ? getTargetSortName() : null, creator);
        }
        return mergeIteratorCreators;
    }

    private String getTargetSortName() {
        return hasTargetSort() ? targetSort.getName() : null;
    }

    private MergeIteratorCreator[] createNormalMergeIteratorCreator() {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            DimensionFilter filter = rowDimension[i].getFilter();
            if (hasTargetSort()) {
                createAllNodeCreator(mergeIteratorCreators, i, filter, targetSort.getName(), new SimpleMergeIteratorCreator());
            } else if (filter == null || filter.canCreateDirectFilter()) {
                mergeIteratorCreators[i] = new SimpleMergeIteratorCreator();
            } else if (filter instanceof DimensionTargetValueFilter) {
                FilterValue filterValue = ((DimensionTargetValueFilter) filter).getFilterValue();
                if (filterValue instanceof StringTOPNFilterValue) {
                    mergeIteratorCreators[i] = new NFilterMergeIteratorCreator(((StringTOPNFilterValue) filterValue).getN());
                } else if (filterValue instanceof StringOneValueFilterValue) {
                    mergeIteratorCreators[i] = new FilterMergeIteratorCreator((StringOneValueFilterValue) filterValue);
                }
            } else {
                createAllNodeCreator(mergeIteratorCreators, i, filter, targetSort.getName(), new SimpleMergeIteratorCreator());
            }
        }
        return mergeIteratorCreators;
    }

    private void createAllNodeCreator(MergeIteratorCreator[] mergeIteratorCreators, int index, DimensionFilter filter, String sortTarget, MergeIteratorCreator creator) {
        List<TargetAndKey>[] metricsToCalculate = new List[mergeIteratorCreators.length];
        Map<String, TargetCalculator> calculatedMap = new HashMap<String, TargetCalculator>();
        Set<String> metrics = new HashSet<String>();
        List<String> usedTargets = new ArrayList<String>();
        boolean hasCalculateMetrics = false;
        if (filter != null) {
            usedTargets = filter.getUsedTargets();
            hasCalculateMetrics = hasCalculateMetrics(usedTargets);
        }
        if (sortTarget != null) {
            usedTargets.add(sortTarget);
        }
        for (String id : usedTargets){
            getRelatedNormalIds(id ,metrics);
        }
        for (CalCalculator cal : configureRelatedCalculators){
            getRelatedNormalIds(cal.getName(), metrics);
        }
        fillMetricsToCalculate(metrics, metricsToCalculate, calculatedMap);
        //如果不过滤计算指标，就直接过滤，过滤计算指标（特别是配置类计算）需要等到全部计算完了猜过滤
        mergeIteratorCreators[index] = new AllNodeMergeIteratorCreator(hasCalculateMetrics ? filter : null, targetSort, metricsToCalculate, calculatedMap, creator);
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

    private IRootDimensionGroup createNormalIteratorRoot() {
        if (usedTargets == null || usedTargets.length == 0) {
            return new NoneMetricRootDimensionGroup(metricGroupInfoList, createNormalMergeIteratorCreator(), session, isRealData, filter, getDirectDimensionFilter());
        }
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null && directFilterIndexes[i] != ALL_SHOW) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(directFilterIndexes[i]));
            }
        }
        return new RootDimensionGroup(metricGroupInfoList, createNormalMergeIteratorCreator(), session, isRealData);
    }

    private PartConstructedRootDimensionGroup createAllNodeIteratorRoot() {
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(directFilterIndexes[i]));
            }
        }
        //全部计算的情况下lastIndex之前的要过滤完再排序，把targetSort去掉
        int lastIndex = getLastAllNodeDimensionIndex();
        return new PartConstructedRootDimensionGroup(metricGroupInfoList, createMergeIteratorCreator(lastIndex), session, isRealData, lastIndex, targetSort, configureRelatedCalculators, getCalculateMetricsDimensionFilters());
    }


    public DimensionFilter[] getCalculateMetricsDimensionFilters() {
        DimensionFilter[] filters = new DimensionFilter[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++){
            if (rowDimension[i].getFilter() != null && hasCalculateMetrics(rowDimension[i].getFilter().getUsedTargets())){
                filters[i] = rowDimension[i].getFilter();
            }
        }
        return filters;
    }

    private boolean hasCalculateMetrics(List<String> usedTargets) {
        for (String id : usedTargets){
            BISummaryTarget target = targetIdMap.get(id);
            if (target != null && !(target.getType() == TargetType.NORMAL)){
                return true;
            }
        }
        return false;
    }

    private int getLastAllNodeDimensionIndex() {
        if (calAllPage || !configureRelatedCalculators.isEmpty() || !targetFilterMap.isEmpty()) {
            return rowDimension.length - 1;
        }
        int index = 0;
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
                    GroupValueIndex filterIndex = resultFilter.createFilterIndex(c, t, session.getLoader(), session.getUserId());
                    retIndexes[i] = retIndexes[i].and(filterIndex);
                }
            }
        }
        return retIndexes;
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

    private boolean hasDimensionInDirectFilter() {
        for (BIDimension dimension : rowDimension) {
            DimensionFilter filter = dimension.getFilter();
            if (filter != null && !filter.canCreateDirectFilter()) {
                return true;
            }
        }
        return !targetFilterMap.isEmpty();
    }

    private boolean hasTargetSort() {
        return targetSort != null;
    }

}
