package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.field.dimension.calculator.NumberDimensionCalculator;
import com.fr.bi.field.dimension.calculator.StringDimensionCalculator;
import com.fr.bi.field.filtervalue.date.evenfilter.DateKeyTargetFilterValue;
import com.fr.bi.field.filtervalue.string.rangefilter.StringINFilterValue;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.key.date.BIDateValue;
import com.fr.bi.stable.data.key.date.BIDateValueFactory;
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/12/26.
 */
public class NoneMetricRootDimensionGroup extends RootDimensionGroup {
    private TargetFilter filter;
    private DimensionCalculator[] dimensionCalculators;
    private DimensionFilter[] directDimensionFilters;

    public NoneMetricRootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, BISession session, boolean useRealData, TargetFilter filter, DimensionFilter[] directDimensionFilters) {
        super(metricGroupInfoList, mergeIteratorCreators, session, useRealData);
        this.filter = filter;
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
        root = NoneDimensionGroup.createDimensionGroup(metrics, summaryLists, tis, gvis, session.getLoader());
    }


    @Override
    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
        GroupValueIndex[] gvis = new GroupValueIndex[1];
        gvis[0] = getFilterIndex(data, deep);
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
                GroupValueIndex pgvi = dktf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), BICubeManager.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
                if (pgvi != null) {
                    gvi = gvi.AND(pgvi);
                }
            } else if (ckp instanceof StringDimensionCalculator) {
                Set currentSet = ((StringDimensionCalculator) ckp).createFilterValueSet((String) value, session.getLoader());
                StringINFilterValue stf = new StringINFilterValue(currentSet);
                BITableRelationPath firstPath = null;
                try {
                    firstPath = BICubeConfigureCenter.getTableRelationManager().getFirstPath(session.getLoader().getUserId(), ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo());
                } catch (BITableUnreachableException e) {
                    continue;
                }
                if (ComparatorUtils.equals(ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo())) {
                    firstPath = new BITableRelationPath();
                }
                if (firstPath == null) {
                    continue;
                }
                GroupValueIndex pgvi = stf.createFilterIndex(new NoneDimensionCalculator(ckp.getField(), BIConfUtils.convert2TableSourceRelation(firstPath.getAllRelations())),
                        ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
                gvi = gvi.AND(pgvi);
            } else if (ckp instanceof NumberDimensionCalculator) {
                BITableRelationPath firstPath = null;
                try {
                    firstPath = BICubeConfigureCenter.getTableRelationManager().getFirstPath(session.getLoader().getUserId(), ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo());
                } catch (BITableUnreachableException e) {
                    continue;
                }
                if (ComparatorUtils.equals(ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo())) {
                    firstPath = new BITableRelationPath();
                }
                if (firstPath == null) {
                    continue;
                }
                ckp.setRelationList(BIConfUtils.convert2TableSourceRelation(firstPath.getAllRelations()));
                GroupValueIndex pgvi = ckp.createNoneSortGroupValueMapGetter(ck.getField().getTableBelongTo(), session.getLoader()).getIndex(value);
                gvi = gvi.AND(pgvi);
            }
            if (filter != null) {
                GroupValueIndex filterGvi = filter.createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
                if (filterGvi != null) {
                    gvi = filterGvi.AND(gvi);
                }
            }
        }
        return gvi;
    }

}
