package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.value.AbstractOriginValueBean;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.dashboard.widget.field.WidgetBeanFieldValue;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.query.adapter.metric.GroupMetric;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.node.cal.CalTargetType;
import com.fr.swift.result.node.cal.TargetCalculatorInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class CalTargetParseUtils {

    /**
     * 计算指标分为如下几类：
     * 1、所有聚合指标，包括3和为了计算4而需要计算的聚合指标
     * 2、所有计算指标，包括4和为了计算4而需要计算的计算指标
     * 3、要展示的聚合指标
     * 4、要展示的计算指标
     * @param widget
     * @return
     * @throws Exception
     */
    public static TargetInfo parseCalTarget(TableWidget widget) throws Exception {
        List<String> metricFieldIds = parseMetricTargetFieldIds(widget);
        List<String> relatedMetricFieldIds = parseRelatedFieldIds(widget, metricFieldIds);
        List<String> calTargetFieldIds = parseCalTargetFieldIds(widget);
        List<String> allTargetFieldIds = new ArrayList<String>();
        allTargetFieldIds.addAll(metricFieldIds);
        allTargetFieldIds.addAll(relatedMetricFieldIds);
        allTargetFieldIds.addAll(calTargetFieldIds);
        List<Integer> targetsForShowKeys = new ArrayList<Integer>();
        List<TargetCalculatorInfo> calculatorInfoList = new ArrayList<TargetCalculatorInfo>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            String fieldId = targets.get(i).getFieldId();
            int resultIndex = allTargetFieldIds.indexOf(fieldId);
            targetsForShowKeys.add(resultIndex);
            WidgetBeanFieldValue value = widget.getFieldByFieldId(fieldId).getCalculate();
            if (value != null) {
                // TODO: 2018/4/8 根据value的type来判断哪类配置类计算
                String originFieldId = ((AbstractOriginValueBean) value).getOrigin();
                int paramIndex = allTargetFieldIds.indexOf(originFieldId);
                calculatorInfoList.add(parseCalInfo(value.getType(), paramIndex, resultIndex));
            }
        }
        metricFieldIds.addAll(relatedMetricFieldIds);
        List<Metric> metrics = createMetrics(metricFieldIds);
        return new TargetInfo(metrics, calculatorInfoList, targetsForShowKeys);
    }

    private static List<Metric> createMetrics(List<String> fieldIds) {
        List<Metric> metrics = new ArrayList<Metric>();
        for (int i = 0; i < fieldIds.size(); i++) {
            metrics.add(toMetric(fieldIds.get(i), i));
        }
        return metrics;
    }

    private static List<String> parseCalTargetFieldIds(TableWidget widget) throws Exception {
        List<String> calTargetFieldIds = new ArrayList<String>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            String fieldId = targets.get(i).getFieldId();
            WidgetBeanFieldValue value = widget.getFieldByFieldId(fieldId).getCalculate();
            if (value != null) {
                calTargetFieldIds.add(fieldId);
            }
        }
        return calTargetFieldIds;
    }

    private static List<String> parseRelatedFieldIds(TableWidget widget, List<String> metricFieldIds) throws Exception {
        List<String> relatedFieldIds = new ArrayList<String>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            String fieldId = targets.get(i).getFieldId();
            WidgetBeanFieldValue value = widget.getFieldByFieldId(fieldId).getCalculate();
            if (value != null) {
                // TODO: 2018/4/8 收集计算指标依赖的聚合指标，暂时假设计算指标不依赖其他计算指标
                // 计算指标依赖的聚合指标字段id。功能给的接口就这样的
                String originFieldId = ((AbstractOriginValueBean) value).getOrigin();
                if (metricFieldIds.indexOf(originFieldId) == -1) {
                    relatedFieldIds.add(originFieldId);
                }
            }
        }
        return relatedFieldIds;
    }

    private static List<String> parseMetricTargetFieldIds(TableWidget widget) throws Exception {
        List<String> metrics = new ArrayList<String>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            String fieldId = targets.get(i).getFieldId();
            WidgetBeanFieldValue value = widget.getFieldByFieldId(fieldId).getCalculate();
            if (value == null) {
                // TODO: 2018/4/8 说明是普通聚合指标或者是快速计算指标，暂时不管快速计算指标的处理！
                metrics.add(fieldId);
            }
        }
        return metrics;
    }

    private static TargetCalculatorInfo parseCalInfo(int type, int paramIndex, int resultIndex) {
        switch (type) {
            case BIDesignConstants.DESIGN.CAL_TARGET.FORMULA:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_SUM_OF_ABOVE);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE_IN_GROUP:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_SUM:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_SUM_OF_ALL);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_AVG:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_AVG);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MAX:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_MAX);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MIN:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_MIN);
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_ASC:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_RANK_ASC);
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_DES:
                return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_RANK_DEC);
        }
        return new TargetCalculatorInfo(paramIndex, resultIndex, CalTargetType.ALL_SUM_OF_ALL);
    }

    private static Metric toMetric(String fieldId, int index) {
        SourceKey key = new SourceKey(fieldId);
        String columnName = SwiftEncryption.decryptFieldId(fieldId)[1];
        ColumnKey colKey = new ColumnKey(columnName);

        // TODO: 2018/3/31 指标的filter属性还没有传过来
        FilterInfo filterInfo = null;
        // TODO: 2018/3/21  暂时不知道targetType如何对应不同聚合类型
        Aggregator agg = new SumAggregate();

        return new GroupMetric(index, key, colKey, filterInfo, agg);
    }
}
