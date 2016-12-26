package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.RootDimensionGroup;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.target.key.cal.BICalculatorTargetKey;
import com.fr.bi.field.target.key.cal.configuration.BIConfiguratedCalculatorTargetKey;
import com.fr.bi.field.target.key.cal.configuration.BIPeriodCalTargetKey;
import com.fr.bi.field.target.target.BISummaryTarget;
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

    private BISession session;
    private List<MetricGroupInfo> metricGroupInfoList = new ArrayList<MetricGroupInfo>();
    private BIDimension[] rowDimension;
    private BISummaryTarget[] usedTargets;
    private Map<String, DimensionFilter> targetFilterMap;
    private NodeExpander expander;
    private boolean isRealData;
    private NameObject targetSort;
    private final boolean showSum;
    private final boolean calAllPage;

    public NodeIteratorCreator(List<MetricGroupInfo> metricGroupInfoList, BIDimension[] rowDimension, BISummaryTarget[] usedTargets, Map<String, DimensionFilter> targetFilterMap, NodeExpander expander, boolean isRealData, BISession session, NameObject targetSort, boolean showSum, boolean setIndex, boolean calAllPage) {
        this.metricGroupInfoList = metricGroupInfoList;
        this.rowDimension = rowDimension;
        this.usedTargets = usedTargets;
        this.targetFilterMap = targetFilterMap;
        this.expander = expander;
        this.isRealData = isRealData;
        this.session = session;
        this.targetSort = targetSort;
        this.showSum = showSum;
        this.calAllPage = calAllPage;
    }

    private CalLevel getConfiguredCalculatorTargetLevel() {
        CalLevel level = CalLevel.PART_NODE;
        for (BICalculatorTargetKey key : LoaderUtils.getCalculatorTargets(usedTargets)) {
            if (key instanceof BIPeriodCalTargetKey) {
                return CalLevel.ALL_NODE;
            }
            if (key instanceof BIConfiguratedCalculatorTargetKey) {
                level = CalLevel.SINGLE_NODE;
            }
        }
        return level;
    }

    public CalLevel getCalLevel() {
        if (calAllPage){
            return CalLevel.ALL_NODE;
        }
        CalLevel level = getConfiguredCalculatorTargetLevel();
        if (level == CalLevel.ALL_NODE){
            return level;
        }
        if (hasDimensionInDirectFilter() && showSum){
            return CalLevel.ALL_NODE;
        }
        return hasTargetSort() ? CalLevel.SINGLE_NODE : level;
    }

    public IRootDimensionGroup createRoot() {
        switch (getCalLevel()) {
            case SINGLE_NODE:
                return createNormalIteratorRoot();
            case PART_NODE:
                return createNormalIteratorRoot();
            default:
                return createNormalIteratorRoot();
        }
    }

    private IRootDimensionGroup createNormalIteratorRoot() {
        GroupValueIndex[] directFilterIndexes = createDirectFilterIndex();
        RootDimensionGroup root = new RootDimensionGroup();
        Map<GroupKey, IRootDimensionGroup> map = new HashMap<GroupKey, IRootDimensionGroup>();
        for (int i = 0; i < directFilterIndexes.length; i++) {
            if (directFilterIndexes[i] != null) {
                MergerInfo mergerInfo = mergerInfoList.get(i);
                mergerInfoList.get(i).getGroupValueIndex().and(directFilterIndexes[i]);
                map.put(mergerInfo.getGroupKey(), mergerInfo.getRootDimensionGroup());
            }
        }
        return mergerInfoList.get(0).getRootDimensionGroup();
    }

    private GroupValueIndex[] createDirectFilterIndex() {
        GroupValueIndex[] retIndexes = new GroupValueIndex[mergerInfoList.size()];
        Arrays.fill(retIndexes, MergerInfo.ALL_SHOW);
        for (int i = 0; i < retIndexes.length; i++) {
            for (int deep = 0; deep < rowDimension.length; deep++) {
                DimensionFilter resultFilter = rowDimension[deep].getFilter();
                if (resultFilter != null && resultFilter.canCreateDirectFilter()) {
                    DimensionCalculator c = mergerInfoList.get(i).createColumnKey()[deep];
                    BusinessTable t = (ComparatorUtils.equals(mergerInfoList.get(i).getRoot().getTableKey(), BIBusinessTable.createEmptyTable())) ? c.getField().getTableBelongTo() : mergerInfoList.get(i).getRoot().getTableKey();
                    GroupValueIndex filterIndex = resultFilter.createFilterIndex(c, t, session.getLoader(), session.getUserId());
                    retIndexes[i] = retIndexes[i].and(filterIndex);
                }
            }
        }
        return retIndexes;
    }


    private boolean hasDimensionInDirectFilter() {
        for (BIDimension dimension : rowDimension){
            DimensionFilter filter = dimension.getFilter();
            if (filter != null && !filter.canCreateDirectFilter()){
                return true;
            }
        }
        return false;
    }

    private boolean hasTargetSort() {
        if (targetSort == null){
            return false;
        }
        for (BISummaryTarget t : usedTargets){
            if (ComparatorUtils.equals(t.getValue(), targetSort.getName())){
                return true;
            }
        }
        return false;
    }

    public void checkExpander(Map<GroupKey, IRootDimensionGroup> expander) {

    }
}
