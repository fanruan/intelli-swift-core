package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.AggregatorAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.AbstractWidgetAdaptor;
import com.fr.swift.adaptor.widget.group.GroupTypeAdaptor;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.aggregator.WrappedAggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.element.metric.FormulaMetric;
import com.fr.swift.query.info.element.metric.GroupMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.element.target.cal.CalTargetType;
import com.fr.swift.query.info.element.target.cal.GroupFormulaTarget;
import com.fr.swift.query.info.element.target.cal.ResultTarget;
import com.fr.swift.query.info.element.target.cal.TargetInfoImpl;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/5/17.
 */
public class TargetInfoUtils {

    public static TargetInfo parse(AbstractTableWidget widget) throws Exception {
        List<Metric> metrics = parseMetric(widget);
        List<GroupTarget> targetCalInfoList = parseTargetCalInfo(widget, metrics);
        Pair<List<Aggregator>, List<ResultTarget>> pair = parseResultAggAndResultTarget(widget, metrics, targetCalInfoList);
        int targetLength = metrics.size() + targetCalInfoList.size();
        return new TargetInfoImpl(targetLength, new ArrayList<Metric>(metrics), targetCalInfoList, pair.getValue(), pair.getKey());
    }

    private static Pair<List<Aggregator>, List<ResultTarget>> parseResultAggAndResultTarget(
            AbstractTableWidget widget, List<Metric> metrics, List<GroupTarget> calInfoList) throws Exception {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        List<ResultTarget> resultTargets = new ArrayList<ResultTarget>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            FineTarget target = targets.get(i);
            int type = target.getType();
            switch (type) {
                case BIDesignConstants.DESIGN.DIMENSION_TYPE.COUNTER:
                case BIDesignConstants.DESIGN.DIMENSION_TYPE.NUMBER:
                case BIDesignConstants.DESIGN.DIMENSION_TYPE.CAL_TARGET: {
                    AggregatorType nodeAggType = AggregatorAdaptor.adaptorMetric(target.getMetric());
                    Metric metric = parseMetric(target, widget).get(0);
                    Aggregator detailAgg = metrics.get(metrics.indexOf(metric)).getAggregator();
                    GroupTarget calInfo = parseTargetCalInfo(0, target, widget, metrics);
                    Aggregator resultAgg;
                    //计算指标不聚合
                    if (calInfo != null) {
                        resultAgg = new WrappedAggregator(AggregatorFactory.createAggregator(AggregatorType.DUMMY));
                    } else if (nodeAggType == AggregatorType.DUMMY) {
                        // 和明细的聚合器相同
                        resultAgg = new WrappedAggregator(detailAgg);
                    } else {
                        resultAgg = new WrappedAggregator(detailAgg, AggregatorFactory.createAggregator(nodeAggType));
                    }
                    int resultFetchIndex;
                    if (calInfo != null) {
                        // 显示快速计算指标
                        resultFetchIndex = metrics.size() + calInfoList.indexOf(calInfo);
                    } else {
                        // 显示聚合指标
                        resultFetchIndex = metrics.indexOf(metric);
                    }
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                    aggregators.add(resultAgg);
                    break;
                }
                default:
                    return Crasher.crash("invalid dimension type of target!");
            }
        }
        return Pair.of(aggregators, resultTargets);
    }

    private static List<GroupTarget> parseTargetCalInfo(AbstractTableWidget widget, List<Metric> metrics) throws Exception {
        Set<GroupTarget> targetCalInfoSet = new LinkedHashSet<GroupTarget>();
        List<FineTarget> targets = widget.getTargetList();
        for (FineTarget target : targets) {
            GroupTarget info = parseTargetCalInfo(metrics.size() + targetCalInfoSet.size(), target, widget, metrics);
            if (info != null) {
                targetCalInfoSet.add(info);
            }
        }
        return new ArrayList<GroupTarget>(targetCalInfoSet);
    }

    private static GroupTarget parseTargetCalInfo(int resultIndex, FineTarget target, AbstractTableWidget widget,
                                                  List<Metric> metrics) {
        int type = target.getType();
        switch (type) {
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.COUNTER:
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.NUMBER: {
                // 计算指标来快速计算
                if (target.getCalculation().getType() != BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE) {
                    // 原始字段生成的指标只能对应一个metric
                    int paramIndex = metrics.indexOf(parseMetric(target, widget).get(0));
                    int rapidType = target.getCalculation().getType();

                    return GroupTargetFactory.createFromRapidTarget(rapidType, 0,
                            new int[]{paramIndex}, resultIndex, getDateDimensionIndexTypePair(widget));
                }
                return null;
            }
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.CAL_TARGET: {
                // TODO: 2018/5/17 SUM_AGG(总金额) + MAX_AGG(购买数量) 这样的公式字段怎么处理呢？
                // 解析成两个metric + 1个计算指标 + ？快速计算吗
                // 计算指标来快速计算
                if (target.getCalculation().getType() != BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE) {
                    // 不包含聚合函数的公式字段生成的指标只能对应一个FormulaMetric
                    int paramIndex = metrics.indexOf(parseMetric(target, widget).get(0));
                    int rapidType = target.getCalculation().getType();
                    // 现在只有计算指标中的排名可能设置了二次计算
                    return GroupTargetFactory.createFromRapidTarget(rapidType, 0, isRepeatCal(target),
                            new int[]{paramIndex}, resultIndex, getDateDimensionIndexTypePair(widget));
                }
                String fieldId = getFieldId(target);
                String formula = AbstractWidgetAdaptor.getFormula(fieldId, widget);
                FilterInfo filterInfo = getFilterInfo(target, widget);
                SourceKey key = new SourceKey(BusinessTableUtils.getSourceIdByTableId(widget.getTableName()));
                if (AggFormulaUtils.isAggFormula(formula)) {
                    List<AggUnit> aggUnits = AggFormulaUtils.getBaseParas(formula);
                    int[] paraIndex = new int[aggUnits.size()];
                    if (aggUnits.size() == 1 && isGroupMetricFormula(aggUnits.get(0).getFormula())) {
                        return null;
                    }
                    for (int i = 0; i < aggUnits.size(); i++) {
                        AggUnit unit = aggUnits.get(i);
                        Aggregator aggregator = AggregatorFactory.createAggregator(unit.getAggregatorType());
                        Metric metric = getFormulaMetric(key, filterInfo, aggregator, unit.getFormula());
                        int index = metrics.indexOf(metric);
                        paraIndex[i] = index;
                        formula = formula.replace(unit.toAGGString(), "${" + index + "}");
                    }
                    return new GroupFormulaTarget(0, resultIndex, paraIndex, CalTargetType.FORMULA, formula);
                }
                return null;
            }
        }
        return Crasher.crash("invalid dimension type of target!");
    }

    private static List<Metric> parseMetric(AbstractTableWidget widget) throws Exception {
        List<FineTarget> targets = widget.getTargetList();
        Set<Metric> metrics = new HashSet<Metric>();
        for (FineTarget target : targets) {
            metrics.addAll(parseMetric(target, widget));
        }
        return new ArrayList<Metric>(metrics);
    }

    private static List<Metric> parseMetric(FineTarget target, AbstractTableWidget widget) {
        List<Metric> metrics = new ArrayList<Metric>();
        int type = target.getType();
        switch (type) {
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.COUNTER:
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.NUMBER: {
                FilterInfo filterInfo = getFilterInfo(target, widget);
                String fieldId = getFieldId(target);
                SourceKey key = new SourceKey(BusinessTableUtils.getSourceIdByTableId(widget.getTableName()));
                if (type == BIDesignConstants.DESIGN.DIMENSION_TYPE.COUNTER && !isDistinctCounter(target)) {
                    metrics.add(new GroupMetric(0, key, new ColumnKey(fieldId), filterInfo, AggregatorFactory.createAggregator(AggregatorType.COUNT)));
                    return metrics;
                }
                AggregatorType aggregatorType = AggregatorAdaptor.adaptorDashBoard(target.getGroup().getType());
                aggregatorType = isDistinctCounter(target) ? AggregatorType.DISTINCT : aggregatorType;
                fieldId = isDistinctCounter(target) ? target.getCounterDep() : fieldId;
                Aggregator aggregator = AggregatorFactory.createAggregator(aggregatorType);
                ColumnKey colKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId));
                metrics.add(new GroupMetric(0, key, colKey, filterInfo, aggregator));
                return metrics;
            }
            case BIDesignConstants.DESIGN.DIMENSION_TYPE.CAL_TARGET: {
                // TODO: 2018/5/17 一个计算指标字段可能对应多个聚合函数（多个GroupMetric），如何区分计算指标是否包含聚合函数呢？
                String fieldId = getFieldId(target);
                String formula = AbstractWidgetAdaptor.getFormula(fieldId, widget);
                FilterInfo filterInfo = getFilterInfo(target, widget);
                SourceKey key = new SourceKey(BusinessTableUtils.getSourceIdByTableId(widget.getTableName()));
                if (AggFormulaUtils.isAggFormula(formula)) {
                    List<AggUnit> aggUnits = AggFormulaUtils.getBaseParas(formula);
                    for (AggUnit unit : aggUnits) {
                        Aggregator aggregator = AggregatorFactory.createAggregator(unit.getAggregatorType());
                        metrics.add(getFormulaMetric(key, filterInfo, aggregator, unit.getFormula()));
                    }
                    return metrics;
                } else {
                    AggregatorType aggregatorType = AggregatorAdaptor.adaptorDashBoard(target.getGroup().getType());
                    Aggregator aggregator = AggregatorFactory.createAggregator(aggregatorType);
                    metrics.add(getFormulaMetric(key, filterInfo, aggregator, formula));
                    return metrics;
                }
            }
        }
        return Crasher.crash("invalid dimension type of target!");
    }

    private static Metric getFormulaMetric(SourceKey key, FilterInfo filterInfo, Aggregator aggregator, String formula) {
        if (isGroupMetricFormula(formula)) {
            String fieldName = FormulaUtils.getRelatedParaNames(formula)[0];
            return new GroupMetric(0, key, new ColumnKey(fieldName), filterInfo, aggregator);
        }
        return new FormulaMetric(0, key, filterInfo,
                aggregator, formula);
    }

    private static boolean isRepeatCal(FineTarget target) {
        try {
//            return target.isRepeatCal();
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isGroupMetricFormula(String formula) {
        String[] paras = FormulaUtils.getRelatedParaNames(formula);
        if (paras.length != 1) {
            return false;
        }
        return formula.replace(paras[0], "").trim().length() == 3;
    }

    private static String getFieldId(FineTarget target) {
        //复制的字段
        if (target.getWidgetBeanField() != null && target.getWidgetBeanField().getSource() != null) {
            return target.getWidgetBeanField().getSource();
        }
        return target.getFieldId();
    }

    private static FilterInfo getFilterInfo(FineTarget target, AbstractTableWidget widget) {
        return target.getDetailFilters() == null ? null :
                FilterInfoFactory.transformFineFilter(widget.getTableName(), target.getDetailFilters());
    }

    private static boolean isDistinctCounter(FineTarget target) {
        String countDep = target.getCounterDep();
//        return StringUtils.isNotEmpty(countDep)
//                && !ComparatorUtils.equals(countDep, BIDesignConstants.DESIGN.COUNTER_DEP.TOTAL_ROWS);
        return false;
    }

    private static List<Pair<Integer, GroupType>> getDateDimensionIndexTypePair(AbstractTableWidget widget) {
        List<Pair<Integer, GroupType>> dateDimension = new ArrayList<Pair<Integer, GroupType>>();
        try {
            String dateFieldId = null;
            List<FineDimension> dimensionList = widget.getDimensionList();
            for (int i = 0; i < dimensionList.size(); i++) {
                FineDimension dimension = dimensionList.get(i);
                if (dimension.getType() == BIDesignConstants.DESIGN.DIMENSION_TYPE.DATE) {
                    if (dateFieldId == null) {
                        dateFieldId = dimension.getFieldId();
                    }
                    if (ComparatorUtils.equals(dateFieldId, dimension.getFieldId())) {
                        GroupType groupType = GroupTypeAdaptor.adaptDashboardGroup(dimension.getGroup().getType());
                        dateDimension.add(new Pair<Integer, GroupType>(i, groupType));
                    }
                }
            }
        } catch (Exception e) {
            return Crasher.crash("get dimension failed", e);
        }
        return dateDimension;
    }
}
