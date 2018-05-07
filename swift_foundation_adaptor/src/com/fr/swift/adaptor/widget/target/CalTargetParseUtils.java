package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.AggregatorAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.target.exception.TargetCircularDependencyException;
import com.fr.swift.query.adapter.metric.CounterMetric;
import com.fr.swift.query.adapter.metric.GroupMetric;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.TargetInfo;
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
import java.util.HashSet;
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
     * 6、基础计算指标字段，不依赖于其他计算指标字段的计算指标字段
     * 7、基础分组表指标，由1或者6拖到分组表中生成的指标，这类指标的特点是不依赖于其他计算指标字段
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
     * 2、发现循环依赖后，短路返回{@link #parseRelationOfCalTargetField(Digraph, String, WidgetBeanField, AbstractTableWidget)}
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
        // 基础的汇总指标，根据(fieldId, (AggregatorType, FilterInfo))进行去重，这样相同的聚合指标只要聚合一次就行了
        // 其中对FilterInfo简单使用equal和hashCode进行比较感觉比较粗暴，理想情况应该对FilterInfo进行布尔化简之后再进行比较
        Set<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetrics = getBaseMetrics(topologicalOrder, widget);
        List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList = new ArrayList<Pair<String, Pair<AggregatorType, FilterInfo>>>(baseMetrics);
        // 解析计算指标信息，List<String>为计算指标对应的id
        // id可能是分组表指标id(对应快速计算指标)或者计算指标字段的fieldId(对应计算指标字段中的计算指标)
        Pair<List<GroupTarget>, List<String>> calTargetInfoPair = parseCalTargetInfo(widget, topologicalOrder, baseMetricList);
        // 从widget#getTargetList中解析出从中间结果中取出查询结果的ResultTarget和对查询结果做最后一步汇总的聚合器
        Pair<List<Aggregator>, List<ResultTarget>> resultInfoPair = parseResultAggregatorAndResultTarget(widget,
                calTargetInfoPair.getValue(), baseMetricList);
        List<Metric> metrics = createMetrics(baseMetricList);
        List<GroupTarget> groupTargets = calTargetInfoPair.getKey();
        int targetLength = baseMetricList.size() + groupTargets.size();
        return new TargetInfoImpl(targetLength, metrics, groupTargets, resultInfoPair.getValue(), resultInfoPair.getKey());
    }

    /**
     * 这边比较无奈了
     */
    private static WidgetBeanField getBeanFieldByFieldId(String id, AbstractTableWidget widget) {
        List<WidgetBeanField> baseBeanFields = widget.widgetBeanFields();
        for (WidgetBeanField field : baseBeanFields) {
            if (StringUtils.equals(field.getId(), id)) {
                return field;
            }
        }
        List<WidgetBeanField> calTargetBeanFields = widget.widgetBeanNewFields();
        for (WidgetBeanField field : calTargetBeanFields) {
            if (StringUtils.equals(field.getId(), id)) {
                return field;
            }
        }
        return null;
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
            if (isBaseFieldTarget(target)) {
                // 原始字段生成的指标
                if (target.getCalculation() == null
                        || target.getCalculation().getType() == BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE) {
                    // 没有设置快速计算指标
                    int resultFetchIndex = baseMetricList.indexOf(parseMetricFromBaseTargetOfBaseField(target, widget));
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                } else {
                    // 设置了快速计算指标的情况，取快速计算指标为结果
                    int resultFetchIndex = calTargetIdList.indexOf(target.getId()) + baseMetricList.size();
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                }
            } else {
                // 计算指标生成的指标
                if (target.getCalculation() == null
                        || target.getCalculation().getType() == BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE) {
                    // 没有设置快速计算指标，先看计算指标字段有没有计算指标，没有的话再去汇总值中找
                    int resultFetchIndex = calTargetIdList.indexOf(target.getWidgetBeanField().getId()) + baseMetricList.size();
                    if (resultFetchIndex == -1) {
                        // 没有对应计算指标的计算指标字段一定是基础计算指标字段，而这个基础计算指标字段只能对应一个聚合指标！
                        resultFetchIndex = baseMetricList.indexOf(parseMetricFromBaseCalTargetField(target.getWidgetBeanField(), widget).toArray()[0]);
                    }
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                } else {
                    // 设置了快速计算的指标，那一定添加了快速计算对应的计算指标
                    int resultFetchIndex = calTargetIdList.indexOf(target.getId()) + baseMetricList.size();
                    resultTargets.add(new ResultTarget(i, resultFetchIndex));
                }
            }
        }
        return Pair.of(aggregators, resultTargets);
    }

    private static Pair<List<GroupTarget>, List<String>> parseCalTargetInfo(AbstractTableWidget widget, List<String> topologicalOrder,
                                                                            List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList) throws Exception {
        List<GroupTarget> calTargets = new ArrayList<GroupTarget>();
        // 当前已经添加的计算指标的id，id可能是分组表指标id或者计算指标字段的fieldId，一个id至多对应一个计算指标
        List<String> addedTargetIdList = new ArrayList<String>();
        // 解析相关的计算指标
        for (String id : topologicalOrder) {
            if (widget.getTargetByTargetId(id) != null) {
                // id对应分组表的指标，可能是原始指标生成的指标，也可能是计算指标生成的指标
                FineTarget target = widget.getTargetByTargetId(id);
                if (!isBaseFieldTarget(target)) {
                    // 计算指标生成的指标
                    // 如果BIDesignConstants.DESIGN.CAL_TARGET为求和、最大值、最小值、平均值，则不要额外的计算指标
                    // 其他BIDesignConstants.DESIGN.CAL_TARGET则需要再求和的基础上加一个计算指标
                    // 感觉计算指标和快速计算两者的功能重复了，计算指标字段依赖于另一个计算指标字段进行汇总和二次计算一样的
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
                if (target.getCalculation() != null
                        && target.getCalculation().getType() != BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE) {
                    // 快速计算指标，这个快速计算指标的常量和计算指标明明可以统一一下的，坑爹！
                    Pair<String, GroupTarget> pair = parseTargetCalInfoFromRapidCalTarget(target, baseMetricList,
                            addedTargetIdList, widget);
                    calTargets.add(pair.getValue());
                    addedTargetIdList.add(pair.getKey());
                }
            } else {
                // 分组表指标依赖的计算指标字段
                Pair<String, GroupTarget> pair = parseTargetCalInfoFromCalTargetField(getBeanFieldByFieldId(id, widget), id,
                        baseMetricList, addedTargetIdList, widget);
                if (pair != null) {
                    calTargets.add(pair.getValue());
                    addedTargetIdList.add(pair.getKey());
                }
            }
        }
        return Pair.of(calTargets, addedTargetIdList);
    }

    private static boolean isBaseFieldTarget(FineTarget target) {
        return target.getType() == BIDesignConstants.DESIGN.DIMENSION_TYPE.COUNTER
                || target.getWidgetBeanField() == null || target.getWidgetBeanField().getTargetIds() == null;
    }

    /**
     * 从指标的快速计算配置中解析计算指标
     */
    private static Pair<String, GroupTarget> parseTargetCalInfoFromRapidCalTarget(
            FineTarget target, List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList,
            List<String> addedTargetIdList, AbstractTableWidget widget) {
        int resultIndex = baseMetricList.size() + addedTargetIdList.size();
        if (isBaseFieldTarget(target)) {
            // 原始字段生成的指标
            int paramIndex = baseMetricList.indexOf(parseMetricFromBaseTargetOfBaseField(target, widget));
            int rapidCalTargetType = target.getCalculation().getType();
            GroupTarget calTarget = GroupTargetFactory.createFromRapidTarget(rapidCalTargetType, 0,
                    new int[]{paramIndex}, resultIndex);
            return Pair.of(target.getId(), calTarget);
        } else {
            // 计算指标字段生成的指标
            // 先确定计算指标字段有没有对应的计算指标
            int paramIndex = addedTargetIdList.indexOf(target.getWidgetBeanField().getId()) + baseMetricList.size();
            if (paramIndex == -1) {
                // 计算指标字段没有没有对应的计算指标(特殊汇总方式的计算指标)，那就找计算指标字段对应的汇总指标，这个汇总指标是唯一的！
                paramIndex = baseMetricList.indexOf(parseMetricFromBaseCalTargetField(target.getWidgetBeanField(), widget).toArray()[0]);
            }
            int rapidCalTargetType = target.getCalculation().getType();
            GroupTarget calTarget = GroupTargetFactory.createFromRapidTarget(rapidCalTargetType, 0,
                    new int[]{paramIndex}, resultIndex);
            return Pair.of(target.getId(), calTarget);
        }
    }

    /**
     * 解析计算指标字段中的计算指标
     *
     * @throws Exception
     */
    private static Pair<String, GroupTarget> parseTargetCalInfoFromCalTargetField(WidgetBeanField field, String id,
                                                                                  List<Pair<String, Pair<AggregatorType, FilterInfo>>> baseMetricList,
                                                                                  List<String> addedTargetIdList, AbstractTableWidget widget) throws Exception {
        int resultIndex = baseMetricList.size() + addedTargetIdList.size();
        if (isIdOfBaseTargetOrBaseCalTargetField(id, widget)) {
            // 不依赖于其他计算指标的计算指标
            int calTargetType = field.getCalculate().getType();
            AggregatorType aggregatorType = AggregatorAdaptor.adaptorCalTarget(calTargetType);
            if (aggregatorType == null) {
                // 说明是求和、极值、平均之外的计算指标。找到当前计算指标字段对应的汇总指标，可能是公式
                List<Pair<String, Pair<AggregatorType, FilterInfo>>> pairs = new ArrayList<Pair<String, Pair<AggregatorType, FilterInfo>>>(
                        parseMetricFromBaseCalTargetField(field, widget));
                int[] paramIndexes = new int[pairs.size()];
                for (int i = 0; i < paramIndexes.length; i++) {
                    paramIndexes[i] = baseMetricList.indexOf(pairs.get(i));
                }
                GroupTarget calTarget = GroupTargetFactory.createFromCalTarget(calTargetType, 0,
                        paramIndexes, resultIndex, field.getCalculate());
                return Pair.of(field.getId(), calTarget);
            }
        } else {
            // 依赖于其他计算指标的计算指标，拓扑排序保证了依赖的计算指标已经添加进来了
            int calTargetType = field.getCalculate().getType();
            List<String> fieldIds = field.getTargetIds();
            int[] paramIndexes = new int[fieldIds.size()];
            // 公式的情况下，可能是计算指标字段和原始字段混合的
            for (int i = 0; i < paramIndexes.length; i++) {
                // 先假设为计算指标字段，先从已经添加的计算指标列表中找到
                int index = addedTargetIdList.indexOf(fieldIds.get(i));
                if (index != -1) {
                    paramIndexes[i] = index + addedTargetIdList.size();
                    continue;
                }
                // 否则当前fieldId对应的字段唯一的聚合指标
                WidgetBeanField subField = getBeanFieldByFieldId(fieldIds.get(i), widget);
                paramIndexes[i] = baseMetricList.indexOf(parseMetricFromBaseCalTargetField(subField, widget).toArray()[0]);
            }
            int paramIndex = addedTargetIdList.indexOf(field.getTargetIds().get(0)) + baseMetricList.size();
            GroupTarget calTarget = GroupTargetFactory.createFromCalTarget(calTargetType, 0,
                    new int[]{paramIndex}, resultIndex, field.getCalculate());
            return Pair.of(field.getId(), calTarget);
        }
        return null;
    }

    /**
     * 解析聚合指标
     * pair的id为原始字段的FieldId
     */
    private static Set<Pair<String, Pair<AggregatorType, FilterInfo>>> getBaseMetrics(List<String> topologicalOrder,
                                                                                      AbstractTableWidget widget) throws Exception {
        Set<Pair<String, Pair<AggregatorType, FilterInfo>>> pairs = new LinkedHashSet<Pair<String, Pair<AggregatorType, FilterInfo>>>();
        for (String id : topologicalOrder) {
            if (!isIdOfBaseTargetOrBaseCalTargetField(id, widget)) {
                // 跳过非基础分组表指标和非基础计算指标字段
                continue;
            }
            // 分组表用到的指标或者是分组表间接用到的指标，统计汇总指标
            FineTarget target = widget.getTargetByTargetId(id);
            if (target != null && isBaseFieldTarget(target)) {
                // 分组表中原始字段生成的指标
                pairs.add(parseMetricFromBaseTargetOfBaseField(target, widget));
            } else if (target != null && !isBaseFieldTarget(target)) {
                // 分组表的基础指标，但是由计算指标字段生成
                pairs.addAll(parseMetricFromBaseCalTargetField(target.getWidgetBeanField(), widget));
            } else {
                // 不属于分组表中指标，但是分组表中的指标依赖的计算指标字段
                pairs.addAll(parseMetricFromBaseCalTargetField(getBeanFieldByFieldId(id, widget), widget));
            }
        }
        return pairs;
    }

    /**
     * 从基础计算指标字段中解析聚合指标，这个计算字段可能是公式
     *
     * @param field 不依赖于其他计算指标字段的计算指标字段
     * @return
     */
    private static Set<Pair<String, Pair<AggregatorType, FilterInfo>>> parseMetricFromBaseCalTargetField(
            WidgetBeanField field, AbstractTableWidget widget) {
        AggregatorType aggregatorType = AggregatorAdaptor.adaptorCalTarget(field.getCalculate().getType());
        // 其他类型的计算指标默认是求和的基础是做的
        aggregatorType = aggregatorType == null ? AggregatorType.SUM : aggregatorType;
        // 找到配置生成的计算字段中的明细过滤信息
        FilterInfo filterInfo = getDetailFilterInfoOfField(widget, field);
        // 因为这个计算指标字段只能依赖原始字段，所以field#getTargetIds()对应的原始字段的fieldId集合
        Set<Pair<String, Pair<AggregatorType, FilterInfo>>> pairs = new HashSet<Pair<String, Pair<AggregatorType, FilterInfo>>>();
        for (String fieldId : field.getTargetIds()) {
            if (getBeanFieldByFieldId(fieldId, widget).getCalculate() == null) {
                pairs.add(Pair.of(fieldId, Pair.of(aggregatorType, filterInfo)));
            } else {
                // 基础计算指标字段
                pairs.add((Pair<String, Pair<AggregatorType, FilterInfo>>) parseMetricFromBaseCalTargetField(
                        getBeanFieldByFieldId(fieldId, widget), widget).toArray()[0]);
            }
        }
        return pairs;
    }

    /**
     * 解析原始字段生成的指标中包含的聚合指标信息
     *
     * @param target 原始字段生成的指标
     * @param widget
     * @return
     */
    private static Pair<String, Pair<AggregatorType, FilterInfo>> parseMetricFromBaseTargetOfBaseField(FineTarget target, AbstractTableWidget widget) {
        // 原始字段生成的指标，id为原始字段的FieldId
        AggregatorType aggregatorType = AggregatorAdaptor.adaptorDashBoard(target.getMetric());
        // TODO: 2018/5/4 target里面的field和widget里面原来的那个field突然间就分裂了，明细过滤再target的field里面！太随性了！
        WidgetBeanField field = target.getWidgetBeanField();
        field = field != null ? field : getBeanFieldByFieldId(target.getFieldId(), widget);
        FilterInfo filterInfo = getDetailFilterInfoOfField(widget, field);
        return Pair.of(target.getFieldId(), Pair.of(aggregatorType, filterInfo));
    }

    /**
     * id对应基础分组表指标或者基础计算指标字段
     */
    private static boolean isIdOfBaseTargetOrBaseCalTargetField(String id, AbstractTableWidget widget) throws Exception {
        if (widget.getTargetByTargetId(id) == null) {
            // 通过计算指标配置面板生成的计算指标字段field，或者是原始字段field。还有可能是组件的field。。
            WidgetBeanField field = getBeanFieldByFieldId(id, widget);
            return field.getCalculate() == null || !isCalTargetFieldDependedOnOtherCalTargetField(field, widget);
        } else {
            FineTarget target = widget.getTargetByTargetId(id);
            if (!isBaseFieldTarget(target)) {
                // 说明target是一个计算指标字段生成的指标。原始字段生成的指标的widgetBeanField为null
                // 检查target有没有依赖其他计算指标
                return !isCalTargetFieldDependedOnOtherCalTargetField(target.getWidgetBeanField(), widget);
            } else {
                // 说明target是通过原始字段拖过来生成的指标，这个指标是不能依赖其他计算指标的
                return true;
            }
        }
    }

    private static boolean isCalTargetFieldDependedOnOtherCalTargetField(WidgetBeanField field, AbstractTableWidget widget) {
        List<String> relatedTargetIds = field.getTargetIds();
        // 检查相关的targetId中有没有计算指标
        for (String targetId : relatedTargetIds) {
            if (getBeanFieldByFieldId(targetId, widget).getCalculate() == null) {
                // targetId对应计算指标的beanFieldId，说明不依赖于其他计算指标
                return false;
            }
        }
        return true;
    }

    /**
     * 解析计算分组表指标的依赖关系，先正向解析，解析完再反转
     * baseFieldId: 原始字段或者基础计算指标字段对应的fieldId
     * baseTargetId: 基础分组表指标对应的targetId
     * 正向解析关系为：{ paths }, path = (baseTargetId -> null) || (targetId -> fieldId -> ... -> baseFieldId)
     * 反转后的关系为：{ paths }, path = (baseTargetId -> null) || (baseFieldId -> fieldId -> ... -> targetId)
     *
     * @param widget
     * @return
     * @throws Exception
     */
    private static Digraph<String> parseTargets2Graph(AbstractTableWidget widget) throws Exception {
        Digraph<String> digraph = new DigraphImpl<String>();
        List<FineTarget> targets = widget.getTargetList();
        for (FineTarget target : targets) {
            digraph.addEdge(target.getId(), null);
            if (isBaseFieldTarget(target)) {
                continue;
            }
            parseRelationOfCalTargetField(digraph, target.getId(), target.getWidgetBeanField(), widget);
        }
        // 做个反转。反转后v -> w，v是基础的计算指标，w是依赖于v的计算指标
        return digraph.reverse();
    }

    /**
     * 解析计算指标字段的依赖关系。如果是基础计算指标字段，则没有额外依赖
     */
    private static void parseRelationOfCalTargetField(Digraph<String> digraph, String previousId,
                                                      WidgetBeanField field, AbstractTableWidget widget) throws Exception {
        if (digraph.hasCycle()) {
            return;
        }
        // field.getTargetIds()得到的是fieldIdList，原始字段的fieldId或者是生成的计算指标字段的fieldId
        for (String id : field.getTargetIds()) {
            if (isIdOfBaseTargetOrBaseCalTargetField(id, widget)) {
                // 当前id已经是最基础的计算指标，不会依赖其他计算指标了
                continue;
            }
            if (getBeanFieldByFieldId(id, widget).getCalculate() != null) {
                // 说明id对应的field是计算指标
                // v -> w, v的计算依赖于w，w是相对基础的计算指标
                digraph.addEdge(previousId, id);
                parseRelationOfCalTargetField(digraph, id, widget.getWidgetBeanField(id), widget);
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

    private static Metric toMetric(int metricIndex, String fieldId, Pair<AggregatorType, FilterInfo> pair) {
        SourceKey key = new SourceKey(fieldId);
        if (isCounterField(fieldId)) {
            return new CounterMetric(metricIndex, key, new ColumnKey(fieldId), pair.getValue());
        }
        String columnName = BusinessTableUtils.getFieldNameByFieldId(fieldId);
        ColumnKey colKey = new ColumnKey(columnName);
        Aggregator aggregator = AggregatorFactory.createAggregator(pair.getKey());
        return new GroupMetric(metricIndex, key, colKey, pair.getValue(), aggregator);
    }

    private static boolean isCounterField(String fieldId) {
        return StringUtils.isNotEmpty(fieldId) && fieldId.endsWith("__count_field_id__");
    }

    /**
     * 从字段中取出明细过滤信息
     *
     * @param field
     * @return
     */
    private static FilterInfo getDetailFilterInfoOfField(AbstractTableWidget widget, WidgetBeanField field) {
        FilterInfo filterInfo = null;
        if (field != null && field.getFilter() != null) {
            filterInfo = FilterInfoFactory.createFilterInfo(widget.getTableName(), field.getFilter(), new ArrayList<Segment>());
        }
        return filterInfo;
    }
}
