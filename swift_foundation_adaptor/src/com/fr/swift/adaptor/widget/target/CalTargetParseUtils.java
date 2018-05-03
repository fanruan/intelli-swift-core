package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.swift.adaptor.transformer.AggregatorAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.target.exception.TargetCircularDependencyException;
import com.fr.swift.query.adapter.metric.GroupMetric;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.adapter.target.cal.GroupTargetImpl;
import com.fr.swift.query.adapter.target.cal.ResultTarget;
import com.fr.swift.query.adapter.target.cal.TargetInfoImpl;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.graph.Digraph;
import com.fr.swift.structure.graph.DigraphImpl;
import com.fr.swift.structure.graph.DigraphUtils;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/8.
 */
public class CalTargetParseUtils {

    /**
     * 解析过程中要区分的几个概念：
     * 1、原始数值字段，业务包表中的数值类字段
     * 2、计算指标字段，通过计算指标配置面板生成的计算指标字段
     * 3、分组表指标，把1或者2拖到分组表中生成的分组表指标
     * 4、聚合指标，通过对明细数据应用某种聚合方式得到。聚合方式比如求和、最小/大值、平均、方差等
     * 5、计算指标，通过对4或者5的结果再进行相关计算的得到
     *
     * 分组表的指标可以分为两类：
     * 1、原始数值字段生成的指标（下面简称：Type1）
     * 2、配置生成的计算指标字段生成的指标（下面简称：Type2）
     *
     * 分组表的计算指标从哪里解析出来：
     * 1、Type1设置了快速计算会生成0个或1个计算指标
     * 2、Type2对应的计算指标字段会生成0个或者多个，同时Type2设置了快速计算会生成0个或1个计算指标
     *
     * 通过计算配置面板生成新的计算指标字段的时候，计算指标字段之间的可以相互依赖。
     * 使用有向图进行解析的步骤：
     * 1、从分组表的指标（最上层）开始递归解析，上层计算指标 -> 基础计算指标
     * 2、发现循环依赖后，短路返回{@link #parseRelatedBeanFields(Digraph, String, WidgetBeanField, AbstractTableWidget)}
     * 3、解析完成之后进行反转，基础计算指标 -> 上层计算指标
     * 4、判断计算指标字段是否存在循环依赖，如果有抛出异常，否则对反转后的有向图进行拓扑排序
     * 5、根据拓扑排序的结果确定对相关聚合指标和计算指标处理的先后顺序
     *
     * @param widget
     * @return
     * @throws Exception
     */
    public static TargetInfo parseCalTarget(AbstractTableWidget widget) throws Exception {
        Digraph<String> digraph = parseTargets2Graph(widget);
        if (digraph.hasCycle()) {
            // 计算指标出现循环依赖的话抛出异常
            throw new TargetCircularDependencyException(digraph, widget);
        }
        // 通过拓扑排序处理好计算指标之间的依赖关系
        List<String> topologicalOrder = DigraphUtils.topologicalOrder(digraph);
        Set<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetrics = getBaseMetrics(topologicalOrder, widget);
        List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList = new ArrayList<Pair<String, Pair<AggregatorType, FilterInfo>>>(baseMetrics);
        Pair<List<GroupTarget>, List<String>> calTargetInfoPair = parseCalTargetInfo(widget, topologicalOrder, baseMetricList);
        Pair<List<Aggregator>, List<ResultTarget>> resultInfoPair = parseResultAggregatorAndResultTarget(widget,
                calTargetInfoPair.getValue(), baseMetricList);
        List<Metric> metrics = createMetrics(baseMetricList);
        List<GroupTarget> groupTargets = calTargetInfoPair.getKey();
        int targetLength = baseMetricList.size() + groupTargets.size();
        return new TargetInfoImpl(targetLength, metrics, groupTargets, resultInfoPair.getValue(), resultInfoPair.getKey());
    }

    private static Pair<List<Aggregator>, List<ResultTarget>> parseResultAggregatorAndResultTarget(
            AbstractTableWidget widget, List<String> calTargetIdList,
            List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList) throws Exception {
        List<FineTarget> targets = widget.getTargetList();
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        List<ResultTarget> resultTargets = new ArrayList<ResultTarget>();
        for (int i = 0; i < targets.size(); i++) {
            FineTarget target = targets.get(i);
            AggregatorType aggregatorType = AggregatorAdaptor.adaptorDashBoard(target.getMetric());
            Aggregator aggregator = AggregatorFactory.createAggregator(aggregatorType);
            aggregators.add(aggregator);
            if (target.getWidgetBeanField() == null) {
                // 原始字段生成的指标
                if (target.getCalculation() == null || target.getCalculation().getType() == BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE) {
                    // 没有设置快速计算指标
                    int resultFetchIndex = baseMetricList.indexOf(parseMetricFromTargetOfTable(target, widget));
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                } else {
                    // 设置了快速计算指标的情况，去快速计算指标为结果
                    int resultFetchIndex = calTargetIdList.indexOf(target.getId()) + baseMetricList.size();
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                }
            } else {
                // 计算指标生成的指标
                int resultFetchIndex = calTargetIdList.indexOf(target.getId()) + baseMetricList.size();
                resultTargets.add(new ResultTarget(i, resultFetchIndex));
            }
        }
        return Pair.of(aggregators, resultTargets);
    }

    private static Pair<List<GroupTarget>, List<String>> parseCalTargetInfo(AbstractTableWidget widget, List<String> topologicalOrder,
                                                                            List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList) throws Exception {
        List<GroupTarget> calTargets = new ArrayList<GroupTarget>();
        // 当前已经添加的计算指标的id
        List<String> addedTargetIdList = new ArrayList<String>();
        // 解析相关的计算指标
        for (String id : topologicalOrder) {
            if (widget.getTargetByTargetId(id) != null) {
                // id对应分组表的指标，可能是原始指标生成的指标，也可能是计算指标生成的指标
                FineTarget target = widget.getTargetByTargetId(id);
                // TODO: 2018/5/1 这边通过判断null来区分还是通过BIDesignConstants.DESIGN.DIMENSION_TYPE.NUMBER这个常量来区分呢？
                if (target.getWidgetBeanField() != null) {
                    // 计算指标生成的指标
                    // 如果BIDesignConstants.DESIGN.CAL_TARGET为求和、最大值、最小值、平均值，则不要额外的计算指标
                    // 其他BIDesignConstants.DESIGN.CAL_TARGET则需要再求和的基础上加一个计算指标
                    // TODO: 2018/5/1 感觉计算指标和快速计算两者的功能重复了，计算指标依赖于另一个计算指标进行汇总和二次计算一样的
                    Pair<String, GroupTarget> pair = parseTargetCalInfoFromCalTargetField(target.getWidgetBeanField(), id,
                            baseMetricList, addedTargetIdList, widget);
                    if (pair != null) {
                        calTargets.add(pair.getValue());
                        addedTargetIdList.add(pair.getKey());
                    }
                }
                // 原始字段生成的指标和计算指标生成的指标都可能设置了快速计算。如果设置了快速计算，则要添加额外的计算指标，否则不需要。
                // 因为快速计算指标依赖于target的汇总值，如果target是通过计算指标生成的指标，那么target的汇总值可能依赖于0个或者1个计算指标
                // 所以快速计算指标要放到最后一步处理！
                if (target.getCalculation() != null) {
                    // 快速计算指标，这个快速计算指标的常量和计算指标明明可以统一一下的，坑爹！
                    // TODO: 2018/5/1 快速计算指标产生的计算指标
                }
            } else {
                // 分组表指标依赖的计算指标字段
                Pair<String, GroupTarget> pair = parseTargetCalInfoFromCalTargetField(widget.getFieldByFieldId(id), id,
                        baseMetricList, addedTargetIdList, widget);
                if (pair != null) {
                    calTargets.add(pair.getValue());
                    addedTargetIdList.add(pair.getKey());
                }
            }
        }
        return Pair.of(calTargets, addedTargetIdList);
    }

    private static Pair<String, GroupTarget> parseTargetCalInfoFromCalTargetField(WidgetBeanField field, String id,
                                                                                  List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList,
                                                                                  List<String> addedTargetIdList, AbstractTableWidget widget) throws Exception {
        int resultIndex = baseMetricList.size() + addedTargetIdList.size();
        if (isBaseTarget(id, widget)) {
            // 不依赖于其他计算指标的计算指标
            int calTargetType = field.getCalculate().getType();
            AggregatorType aggregatorType = AggregatorAdaptor.adaptorCalTarget(calTargetType);
            if (aggregatorType == null) {
                // 说明是求和、极值、平均之外的计算指标。找到当前计算指标字段对应的汇总指标
                int paramIndex = baseMetricList.indexOf(parseMetricFromField(field));
                GroupTarget calTarget = parseCalInfo(calTargetType, 0, new int[]{paramIndex}, resultIndex);
                return Pair.of(id, calTarget);
            }
        } else {
            // 依赖于其他计算指标的计算指标，拓扑排序保证了依赖的计算指标已经添加进来了
            int calTargetType = field.getCalculate().getType();
            // TODO: 2018/5/1 不考虑公式的情况
            int paramIndex = addedTargetIdList.indexOf(field.getTargetIds().get(0));
            GroupTarget calTarget = parseCalInfo(calTargetType, 0, new int[]{paramIndex}, resultIndex);
            return Pair.of(id, calTarget);
        }
        return null;
    }

    private static Set<Pair<String, Pair<AggregatorType, FilterInfo>>> getBaseMetrics(List<String> topologicalOrder,
                                                                                      AbstractTableWidget widget) throws Exception {
        Set<Pair<String, Pair<AggregatorType, FilterInfo>>> pairs = new LinkedHashSet<Pair<String, Pair<AggregatorType, FilterInfo>>>();
        for (String id : topologicalOrder) {
            if (!isBaseTarget(id, widget)) {
                continue;
            }
            // 分组表用到的指标或者是分组表间接用到的指标，统计汇总指标
            if (widget.getTargetByTargetId(id) != null) {
                // 分组表中包含的指标，可能是字段生成的指标，也可能是计算指标生成的指标
                FineTarget target = widget.getTargetByTargetId(id);
                pairs.add(parseMetricFromTargetOfTable(target, widget));
            } else {
                // 不在分组表中并且分组表中的指标依赖的计算指标
                pairs.add(parseMetricFromField(widget.getFieldByFieldId(id)));
            }
        }
        return pairs;
    }

    private static Pair<String, Pair<AggregatorType, FilterInfo>> parseMetricFromField(WidgetBeanField field) {
        AggregatorType aggregatorType = AggregatorAdaptor.adaptorCalTarget(field.getCalculate().getType());
        aggregatorType = aggregatorType == null ? AggregatorType.SUM : aggregatorType;
        FilterInfo filterInfo = field.getFilter() == null ? null : FilterInfoFactory.createFilterInfo(null, field.getFilter(), new ArrayList<Segment>());
        // TODO: 2018/5/1 公式的情况另外处理吧
        return Pair.of(field.getTargetIds().get(0), Pair.of(aggregatorType, filterInfo));
    }

    private static Pair<String, Pair<AggregatorType, FilterInfo>> parseMetricFromTargetOfTable(FineTarget target, AbstractTableWidget widget) {
        if (target.getWidgetBeanField() == null) {
            // 原始字段生成的指标，id为原始字段的FieldId
            AggregatorType aggregatorType = AggregatorAdaptor.adaptorDashBoard(target.getMetric());
            FilterInfo filterInfo = getDetailFilterInfoOfTarget(target, widget);
            return Pair.of(target.getFieldId(), Pair.of(aggregatorType, filterInfo));
        } else {
            // 计算指标生成的指标（这个指标在分组表中做二次计算），id为targetId，对应生成字段fieldId
            WidgetBeanField field = target.getWidgetBeanField();
            // 找到配置生成的计算指标的字段中的汇总类型
            AggregatorType aggregatorType = AggregatorAdaptor.adaptorCalTarget(field.getCalculate().getType());
            // 其他类型的计算指标默认是求和的基础是做的
            aggregatorType = aggregatorType == null ? AggregatorType.SUM : aggregatorType;
            // 找到配置生成的计算字段中的明细过滤信息
            FilterInfo filterInfo = getDetailFilterInfoOfTarget(target, widget);
            // 对应的原始字段的fieldId
            String fieldId = field.getTargetIds().get(0);
            return Pair.of(fieldId, Pair.of(aggregatorType, filterInfo));
        }
    }

    private static boolean isBaseTarget(String id, AbstractTableWidget widget) throws Exception {
        if (widget.getTargetByTargetId(id) == null) {
            // 通过计算指标配置面板生成的计算指标字段field，或者是原始字段field
            WidgetBeanField field = widget.getFieldByFieldId(id);
            return field.getCalculate() == null || !isCalTargetDependedOnOtherCalTarget(field, widget);
        } else {
            FineTarget target = widget.getTargetByTargetId(id);
            if (target.getWidgetBeanField() != null) {
                // 说明target是一个计算指标，原始指标的widgetBeanField为null
                // 检查target有没有依赖其他计算指标
                return !isCalTargetDependedOnOtherCalTarget(target.getWidgetBeanField(), widget);
            } else {
                // 说明target是通过原始字段拖过来生成的指标，这个指标是不能依赖其他计算指标的
                return true;
            }
        }
    }

    private static boolean isCalTargetDependedOnOtherCalTarget(WidgetBeanField field, AbstractTableWidget widget) {
        List<String> relatedTargetIds = field.getTargetIds();
        // 检查相关的targetId中有没有计算指标
        for (String targetId : relatedTargetIds) {
            if (widget.getFieldByFieldId(targetId).getCalculate() == null) {
                // targetId对应计算指标的beanFieldId，说明不依赖于其他计算指标
                return false;
            }
        }
        return true;
    }

    private static Digraph<String> parseTargets2Graph(AbstractTableWidget widget) throws Exception {
        Digraph<String> digraph = new DigraphImpl<String>();
        List<FineTarget> targets = widget.getTargetList();
        for (FineTarget target : targets) {
            digraph.addEdge(target.getId(), null);
            if (target.getWidgetBeanField() == null) {
                continue;
            }
            parseRelatedBeanFields(digraph, target.getId(), target.getWidgetBeanField(), widget);
        }
        // 做个反转。反转后v -> w，v是基础的计算指标，w是依赖于v的计算指标
        return digraph.reverse();
    }

    private static void parseRelatedBeanFields(Digraph<String> digraph, String preField,
                                               WidgetBeanField field, AbstractTableWidget widget) throws Exception {
        if (digraph.hasCycle()) {
            return;
        }
        for (String id : field.getTargetIds()) {
            if (isBaseTarget(id, widget)) {
                // 当前id已经是最基础的计算指标，不会依赖其他计算指标了
                continue;
            }
            // v -> w, v的计算依赖于w，w是相对基础的计算指标
            digraph.addEdge(preField, id);
            if (widget.getFieldByFieldId(id).getCalculate() != null) {
                // 说明id对应的field是计算指标
                parseRelatedBeanFields(digraph, id, widget.getWidgetBeanField(id), widget);
            }
        }
    }

    private static List<Metric> createMetrics(List<Pair<String, Pair<AggregatorType, FilterInfo>>> metricPairs) {
        List<Metric> metrics = new ArrayList<Metric>();
        for (int i = 0; i < metricPairs.size(); i++) {
            metrics.add(toMetric(i, metricPairs.get(i).getKey(), metricPairs.get(i).getValue()));
        }
        return metrics;
    }

    private static GroupTarget parseCalInfo(int type, int queryIndex, int[] paramIndexes, int resultIndex) {
        switch (type) {
            case BIDesignConstants.DESIGN.CAL_TARGET.FORMULA:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ABOVE);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE_IN_GROUP:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_SUM:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_AVG:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_AVG);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MAX:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MAX);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MIN:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MIN);
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_ASC:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_ASC);
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_DES:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_DEC);
        }
        return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL);
    }

    private static Metric toMetric(int metricIndex, String fieldId, Pair<AggregatorType, FilterInfo> pair) {
        SourceKey key = new SourceKey(fieldId);
        String columnName = BusinessTableUtils.getFieldNameByFieldId(fieldId);
        ColumnKey colKey = new ColumnKey(columnName);
        Aggregator aggregator = AggregatorFactory.createAggregator(pair.getKey());
        return new GroupMetric(metricIndex, key, colKey, pair.getValue(), aggregator);
    }

    private static FilterInfo getDetailFilterInfoOfTarget(FineTarget target, AbstractTableWidget widget) {
        FilterInfo filterInfo = null;
        WidgetBeanField beanField;
        if (target.getWidgetBeanField() != null) {
            // target为计算指标
            // TODO: 2018/5/1 要不要把target依赖的原始字段的beanField里面的过滤条件拿过来呢？
            // TODO: 2018/5/1 如果target依赖于另一个计算指标，要不要把依赖的target的beanField里面的明细过滤拿过来呢？
            beanField = target.getWidgetBeanField();
        } else {
            // target为通过原始字段生成的指标，把原始字段的beanField里面的过滤信息拿过来
            // TODO: 2018/5/1 target#getFieldId和target#getId都是原始字段的FieldId吗？
            beanField = widget.getFieldByFieldId(target.getFieldId());
        }
        if (beanField != null && beanField.getFilter() != null) {
            filterInfo = FilterInfoFactory.createFilterInfo(widget.getTableName(), beanField.getFilter(), new ArrayList<Segment>());
        }
        return filterInfo;
    }
}
