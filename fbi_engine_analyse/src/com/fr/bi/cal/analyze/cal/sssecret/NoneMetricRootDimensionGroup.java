package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.UserAnalysisCubeDataLoaderCreator;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.field.dimension.calculator.NumberDimensionCalculator;
import com.fr.bi.field.dimension.calculator.StringDimensionCalculator;
import com.fr.bi.field.filtervalue.date.evenfilter.DateKeyTargetFilterValue;
import com.fr.bi.field.filtervalue.string.rangefilter.StringINFilterValue;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.key.date.BIDateValue;
import com.fr.bi.stable.data.key.date.BIDateValueFactory;
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/12/26.
 */
public class NoneMetricRootDimensionGroup extends RootDimensionGroup {

    private TargetFilter filter;

    private List<TargetFilter> authFilter;

    private DimensionCalculator[] dimensionCalculators;

    private DimensionFilter[] directDimensionFilters;

    //过滤的地方缓存下NoneDimensionCalculator，要不loadgroup次数太多了，硬盘渣的情况下判断cube exist 太卡。
    private Map<CachedNoneDimensionCalculatorKey, DimensionCalculator> noneDimensionCalculatorMap = new HashMap<CachedNoneDimensionCalculatorKey, DimensionCalculator>();

    protected NoneMetricRootDimensionGroup() {

    }

    public NoneMetricRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, NodeCreator nodeCreator, int sumLength, BISession session, boolean useRealData, TargetFilter filter, List<TargetFilter> authFilter, DimensionFilter[] directDimensionFilters) {

        super(metricGroupInfoList, mergeIteratorCreators, nodeCreator, sumLength, session, useRealData);
        this.filter = filter;
        this.authFilter = authFilter;
        this.directDimensionFilters = directDimensionFilters;
    }

    protected void initGetterAndRows() {

        super.initGetterAndRows();
        this.dimensionCalculators = new DimensionCalculator[rowSize];
        for (int i = 0; i < rowSize; i++) {
            this.dimensionCalculators[i] = columns[i][0];
        }
    }

    protected void initRoot() {

        metrics = new BusinessTable[metricGroupInfoList.size()];
        summaryLists = new ArrayList[0];
        GroupValueIndex[] gvis = new GroupValueIndex[metricGroupInfoList.size()];
        for (int i = 0; i < metricGroupInfoList.size(); i++) {
            metrics[i] = dimensionCalculators[0].getField().getTableBelongTo();
            gvis[i] = metricGroupInfoList.get(i).getFilterIndex();
        }
        root = NoneDimensionGroup.createDimensionGroup(metrics, summaryLists, sumLength, tis, nodeCreator.createMetricMergeResult(null, sumLength, gvis), session.getLoader());
    }


    @Override
    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {

        GroupValueIndex[] gvis = new GroupValueIndex[1];
        gvis[0] = getFilterIndex(data, deep);
        if (gvis[0].isAllEmpty() || ng == NoneDimensionGroup.EMPTY) {
            return new EmptySingleDimensionGroup(data, deep);
        }
        return ng.createSingleDimensionGroup(columns[deep], getters[deep], data, mergeIteratorCreators[deep], gvis, useRealData);
    }

    private GroupValueIndex getFilterIndex(Object[] values, int deep) {

        DimensionCalculator ck = dimensionCalculators[deep];
        GroupValueIndex gvi = session.createFilterGvi(ck.getField().getTableBelongTo());
        if (directDimensionFilters[deep] != null) {
            gvi = gvi.AND(directDimensionFilters[deep].createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId()));
        }
        int i = deep;
        while (i != 0) {
            i--;
            DimensionCalculator ckp = dimensionCalculators[i];
            Object value = values[i];
            if (value == null || ckp.getRelationList() == null || value == BIBaseConstant.EMPTY_NODE_DATA) {
                continue;
            }
            if (ckp instanceof DateDimensionCalculator) {
                gvi = getDateFilterIndex(ck, gvi, ckp, value);
            } else if (ckp instanceof StringDimensionCalculator) {
                Set currentSet = ((StringDimensionCalculator) ckp).createFilterValueSet((String) value, session.getLoader());
                StringINFilterValue stf = new StringINFilterValue(currentSet);
                BITableRelationPath firstPath = getBiTableRelationPath(ck, ckp);
                if (firstPath == null) {
                    continue;
                }
                GroupValueIndex pgvi = stf.createFilterIndex(getCachedNoneDimensionCalculator(i, deep, ckp.getField(), BIConfUtils.convert2TableSourceRelation(firstPath.getAllRelations())),
                                                             ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
                gvi = gvi.AND(pgvi);
            } else if (ckp instanceof NumberDimensionCalculator) {
                BITableRelationPath firstPath = getBiTableRelationPath(ck, ckp);
                if (firstPath == null) {
                    continue;
                }
                GroupValueIndex pgvi = getCachedNumberDimensionCalculator(i, deep, (NumberDimensionCalculator) ckp, BIConfUtils.convert2TableSourceRelation(firstPath.getAllRelations())).createNoneSortGroupValueMapGetter(ck.getField().getTableBelongTo(), session.getLoader()).getIndex(value);
                gvi = gvi.AND(pgvi);
            }
        }
        if (filter != null) {
            GroupValueIndex filterGvi = filter.createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
            if (filterGvi != null) {
                gvi = filterGvi.AND(gvi);
            }
        }
        if (authFilter != null) {
            for (TargetFilter aFilter : authFilter) {
                GroupValueIndex filterGvi = aFilter.createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
                if (filterGvi != null) {
                    gvi = filterGvi.AND(gvi);
                }
            }
        }
        return gvi;
    }

    private BITableRelationPath getBiTableRelationPath(DimensionCalculator ck, DimensionCalculator ckp) {

        BITableRelationPath firstPath = null;
        try {
            firstPath = BICubeConfigureCenter.getTableRelationManager().getFirstPath(session.getLoader().getUserId(), ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo());
            if (ComparatorUtils.equals(ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo())) {
                firstPath = new BITableRelationPath();
            }
        } catch (BITableUnreachableException e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return firstPath;
    }

    private GroupValueIndex getDateFilterIndex(DimensionCalculator ck, GroupValueIndex gvi, DimensionCalculator ckp, Object value) {

        Set<BIDateValue> currentSet = new HashSet<BIDateValue>();
        /**
         * 螺旋分析这里会出现空字符串
         */
        if (value instanceof Number) {
            currentSet.add(BIDateValueFactory.createDateValue(ckp.getGroup().getType(), (Number) value));
        } else {
            currentSet.add(null);
        }

        DateKeyTargetFilterValue dktf = new DateKeyTargetFilterValue(((DateDimensionCalculator) ckp).getGroupDate(), currentSet);
        GroupValueIndex pgvi = dktf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), UserAnalysisCubeDataLoaderCreator.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
        if (pgvi != null) {
            gvi = gvi.AND(pgvi);
        }
        return gvi;
    }

    @Override
    public IRootDimensionGroup createClonedRoot() {

        NoneMetricRootDimensionGroup root = (NoneMetricRootDimensionGroup) super.createClonedRoot();
        root.filter = filter;
        root.dimensionCalculators = dimensionCalculators;
        root.directDimensionFilters = directDimensionFilters;
        return root;
    }

    @Override
    protected IRootDimensionGroup createNew() {

        return new NoneMetricRootDimensionGroup();
    }

    //这个是单线程执行的
    private DimensionCalculator getCachedNoneDimensionCalculator(int pIndex, int index, BusinessField field, List<BITableSourceRelation> relation) {

        CachedNoneDimensionCalculatorKey key = new CachedNoneDimensionCalculatorKey(pIndex, index);
        if (!noneDimensionCalculatorMap.containsKey(key)) {
            noneDimensionCalculatorMap.put(key, new CachedNoneDimensionCalculator(field, relation));
        }
        return noneDimensionCalculatorMap.get(key);
    }

    //这个是单线程执行的
    private DimensionCalculator getCachedNumberDimensionCalculator(int pIndex, int index, NumberDimensionCalculator numberDimensionCalculator, List<BITableSourceRelation> relation) {

        CachedNoneDimensionCalculatorKey key = new CachedNoneDimensionCalculatorKey(pIndex, index);
        if (!noneDimensionCalculatorMap.containsKey(key)) {
            noneDimensionCalculatorMap.put(key, new CachedNumberDimensionCalculator(numberDimensionCalculator, relation));
        }
        return noneDimensionCalculatorMap.get(key);
    }

    private class CachedNoneDimensionCalculatorKey {

        private int pIndex;

        private int index;

        public CachedNoneDimensionCalculatorKey(int pIndex, int index) {

            this.pIndex = pIndex;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CachedNoneDimensionCalculatorKey that = (CachedNoneDimensionCalculatorKey) o;

            if (pIndex != that.pIndex) {
                return false;
            }
            return index == that.index;
        }

        @Override
        public int hashCode() {

            int result = pIndex;
            result = 31 * result + index;
            return result;
        }
    }
}
