package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.value.AbstractOriginValueBean;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.dashboard.widget.field.WidgetBeanFieldValue;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.query.adapter.metric.GroupMetric;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.adapter.target.cal.ResultTarget;
import com.fr.swift.query.adapter.target.cal.GroupTargetImpl;
import com.fr.swift.query.adapter.target.cal.TargetInfoImpl;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.DummyAggregator;
import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.utils.BusinessTableUtils;

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
     *
     * @param widget
     * @return
     * @throws Exception
     */
    public static TargetInfoImpl parseCalTarget(AbstractTableWidget widget) throws Exception {
        List<String> metricFieldIds = parseMetricTargetFieldIds(widget);
        List<String> relatedMetricFieldIds = parseRelatedFieldIds(widget, metricFieldIds);
        List<String> calTargetFieldIds = parseCalTargetFieldIds(widget);
        List<String> allTargetFieldIds = new ArrayList<String>();
        allTargetFieldIds.addAll(metricFieldIds);
        allTargetFieldIds.addAll(relatedMetricFieldIds);
        allTargetFieldIds.addAll(calTargetFieldIds);
        List<ResultTarget> targetsForShowList = new ArrayList<ResultTarget>();
        List<Aggregator> aggregatorListForResultTargetMerging = new ArrayList<Aggregator>();
        List<GroupTarget> calculatorInfoList = new ArrayList<GroupTarget>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            String fieldId = targets.get(i).getFieldId();
            int resultIndex = allTargetFieldIds.indexOf(fieldId);
            // TODO: 2018/4/9 ResultTarget的queryColumnIndex没有加上维度的个数
            targetsForShowList.add(new ResultTarget(i, resultIndex));
            WidgetBeanFieldValue value = widget.getFieldByFieldId(fieldId).getCalculate();
            if (value != null) {
                // TODO: 2018/4/8 根据value的type来判断哪类配置类计算
                String originFieldId = ((AbstractOriginValueBean) value).getOrigin();
                int paramIndex = allTargetFieldIds.indexOf(originFieldId);
                calculatorInfoList.add(parseCalInfo(value.getType(), i, new int[] {paramIndex}, resultIndex));
            }

            // TODO: 2018/4/11 指标结果合并用到的Aggregator，配置类计算的结果如何合并还没定
            if (value == null) {
                // 聚合指标，暂时都是SumAggregate
                aggregatorListForResultTargetMerging.add(new SumAggregate());
            } else {
                // 配置类计算的结果指标不汇总
                aggregatorListForResultTargetMerging.add(new DummyAggregator());
            }
        }
        metricFieldIds.addAll(relatedMetricFieldIds);
        List<Metric> metrics = createMetrics(metricFieldIds, widget);
        // TODO: 2018/4/11 这边需要提供targetsFowShowList对应的Aggregator，用于结果的聚合
        return new TargetInfoImpl(metrics, calculatorInfoList, targetsForShowList, aggregatorListForResultTargetMerging);
    }

    private static List<Metric> createMetrics(List<String> fieldIds, AbstractTableWidget widget) throws Exception {
        List<Metric> metrics = new ArrayList<Metric>();
        for (int i = 0; i < fieldIds.size(); i++) {
            metrics.add(toMetric(fieldIds.get(i), i, widget.getTargetList()));
        }
        return metrics;
    }

    private static List<String> parseCalTargetFieldIds(AbstractTableWidget widget) throws Exception {
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

    private static List<String> parseRelatedFieldIds(AbstractTableWidget widget, List<String> metricFieldIds) throws Exception {
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

    private static List<String> parseMetricTargetFieldIds(AbstractTableWidget widget) throws Exception {
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

    private static GroupTarget parseCalInfo(int type, int queryIndex, int[] paramIndexes, int resultIndex) {
        switch (type) {
            case BIDesignConstants.DESIGN.CAL_TARGET.FORMULA:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ABOVE, new DummyAggregator());
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE_IN_GROUP:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_SUM:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL, new DummyAggregator());
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_AVG:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_AVG, new DummyAggregator());
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MAX:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MAX, new DummyAggregator());
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MIN:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MIN, new DummyAggregator());
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_ASC:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_ASC, new DummyAggregator());
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_DES:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_DEC, new DummyAggregator());
        }
        return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL, new DummyAggregator());
    }

    private static Metric toMetric(String fieldId, int index, List<FineTarget> targetList) {
        SourceKey key = new SourceKey(fieldId);
        String columnName = BusinessTableUtils.getFieldNameByFieldId(fieldId);
        ColumnKey colKey = new ColumnKey(columnName);

        // TODO: 2018/3/31 指标的filter属性还没有传过来
        FilterInfo filterInfo = null;
        WidgetBeanField beanField = targetList.get(index).getWidgetBeanField();
        if (beanField != null){
            FilterBean filterBean = beanField.getFilter();
            if (filterBean != null){
                filterInfo = FilterInfoFactory.createFilterInfo(filterBean, new ArrayList<Segment>());
            }
        }
        // TODO: 2018/3/21  暂时不知道targetType如何对应不同聚合类型
        Aggregator agg = new SumAggregate();

        return new GroupMetric(index, key, colKey, filterInfo, agg);
    }
}
