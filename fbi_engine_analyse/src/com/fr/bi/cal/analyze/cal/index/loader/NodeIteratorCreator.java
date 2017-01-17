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
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
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
    private final boolean calAllPage;

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
        this.calAllPage = calAllPage;
    }

    public CalLevel getCalLevel() {
        if (calAllPage || !LoaderUtils.getCalculatorTargets(usedTargets).isEmpty()) {
            return CalLevel.ALL_NODE;
        }
        if (hasDimensionInDirectFilter() && showSum) {
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

    private MergeIteratorCreator[] createMergeIteratorCreator() {
        MergeIteratorCreator[] mergeIteratorCreators = new MergeIteratorCreator[rowDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            DimensionFilter filter = rowDimension[i].getFilter();
            if (hasTargetSort()) {
                createAllNodeCreator(mergeIteratorCreators, i, filter);
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
                createAllNodeCreator(mergeIteratorCreators, i, filter);
            }
        }
        return mergeIteratorCreators;
    }

    private void createAllNodeCreator(MergeIteratorCreator[] mergeIteratorCreators, int index, DimensionFilter filter) {
        List<TargetAndKey>[] metricsToCalculate = new List[mergeIteratorCreators.length];
        Map<String, TargetCalculator> calculatedMap = new HashMap<String, TargetCalculator>();
        List<String> metrics = new ArrayList<String>();
        if (filter != null){
            metrics = filter.getUsedTargets();
        }
        if (targetSort != null){
            metrics.add(targetSort.getName());
        }
        fillMetricsToCalculate(metrics, metricsToCalculate, calculatedMap);
        mergeIteratorCreators[index] = new AllNodeMergeIteratorCreator(filter, targetSort, metricsToCalculate, calculatedMap);
    }

    private void fillMetricsToCalculate(List<String> metrics, List<TargetAndKey>[] metricsToCalculate, Map<String, TargetCalculator> calculatedMap) {
        for (int i = 0; i < metricGroupInfoList.size(); i++){
            MetricGroupInfo info = metricGroupInfoList.get(i);
            List<TargetAndKey> calList = new ArrayList<TargetAndKey>();
            metricsToCalculate[i] = calList;
            List<TargetAndKey> list = info.getSummaryList();
            for (TargetAndKey targetAndKey : list){
                if (metrics.contains(targetAndKey.getTargetId())){
                    calculatedMap.put(targetAndKey.getTargetId(), targetAndKey.getCalculator());
                    calList.add(targetAndKey);
                }
            }
        }
    }

    private IRootDimensionGroup createNormalIteratorRoot() {
        if (usedTargets == null || usedTargets.length == 0) {
            return new NoneMetricRootDimensionGroup(metricGroupInfoList, createMergeIteratorCreator(), session, isRealData, filter, getDirectDimensionFilter());
        }
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(directFilterIndexes[i]));
            }
        }
        return new RootDimensionGroup(metricGroupInfoList, createMergeIteratorCreator(), session, isRealData);
    }

    private IRootDimensionGroup createAllNodeIteratorRoot() {
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null) {
                metricGroupInfoList.get(i).setFilterIndex(metricGroupInfoList.get(i).getFilterIndex().AND(directFilterIndexes[i]));
            }
        }
        return new PartConstructedRootDimensionGroup(metricGroupInfoList, createMergeIteratorCreator(), session, isRealData, getLastAllNodeDimensionIndex());
    }

    private int getLastAllNodeDimensionIndex() {
        if (calAllPage || !LoaderUtils.getCalculatorTargets(usedTargets).isEmpty() || !targetFilterMap.isEmpty()) {
            return rowDimension.length - 1;
        }
        int index = 0;
        for (int i = 0; i < rowDimension.length; i++){
            if (rowDimension[i].getFilter() != null && !rowDimension[i].getFilter().canCreateDirectFilter()){
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
        if (targetSort == null) {
            return false;
        }
        for (BISummaryTarget t : usedTargets) {
            if (ComparatorUtils.equals(t.getValue(), targetSort.getName())) {
                return true;
            }
        }
        return false;
    }
}
