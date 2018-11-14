package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.constant.BIDesignConstants;
import com.fr.swift.query.aggregator.AggregatorType;

/**
 * Created by pony on 2017/12/26.
 */
public class AggregatorAdaptor {
    public static AggregatorType transformAggregatorType(int fieldType, int aggregatorType) {
        switch (fieldType) {
            case BICommonConstants.COLUMN.NUMBER:
                return getNumberAggregatorType(aggregatorType);
            case BICommonConstants.COLUMN.DATE:
                return getDateAggregatorType(aggregatorType);
            default:
                return getStringAggregatorType(aggregatorType);
        }
    }

    private static AggregatorType getStringAggregatorType(int aggregatorType) {
        switch (aggregatorType) {
            case BIConfConstants.CONF.GROUP.STRING.COUNT:
                return AggregatorType.DISTINCT;
            case BIConfConstants.CONF.GROUP.STRING.APPEND:
                return AggregatorType.STRING_COMBINE;
            default:
                return AggregatorType.COUNT;
        }
    }

    private static AggregatorType getDateAggregatorType(int aggregatorType) {
        switch (aggregatorType) {
            case BIConfConstants.CONF.GROUP.DATE.COUNT:
                return AggregatorType.DISTINCT;
            case BIConfConstants.CONF.GROUP.DATE.ET:
                return AggregatorType.DATE_MIN;
            case BIConfConstants.CONF.GROUP.DATE.LT:
                return AggregatorType.DATE_MAX;
            default:
                return AggregatorType.COUNT;
        }
    }

    private static AggregatorType getNumberAggregatorType(int aggregatorType) {
        switch (aggregatorType) {
            case BIConfConstants.CONF.GROUP.NUMBER.SUM:
                return AggregatorType.SUM;
            case BIConfConstants.CONF.GROUP.NUMBER.AVG:
                return AggregatorType.AVERAGE;
            case BIConfConstants.CONF.GROUP.NUMBER.MEDIAN:
                return AggregatorType.MEDIAN;
            case BIConfConstants.CONF.GROUP.NUMBER.MAX:
                return AggregatorType.MAX;
            case BIConfConstants.CONF.GROUP.NUMBER.MIN:
                return AggregatorType.MIN;
            case BIConfConstants.CONF.GROUP.NUMBER.STANDARD_DEVIATION:
                return AggregatorType.STANDARD_DEVIATION;
            case BIConfConstants.CONF.GROUP.NUMBER.VARIANCE:
                return AggregatorType.VARIANCE;
            case BIConfConstants.CONF.GROUP.NUMBER.COUNT:
                return AggregatorType.DISTINCT;
            default:
                return AggregatorType.COUNT;
        }
    }

    /**
     * nice job foundation
     * @param aggregatorType
     * @return
     */
    public static AggregatorType transformAllValuesAggregatorType(int aggregatorType) {
        switch (aggregatorType) {
            case BIConfConstants.CONF.ADD_COLUMN.ALL_VALUES.SUMMARY_TYPE.SUM:
                return AggregatorType.SUM;
            case BIConfConstants.CONF.ADD_COLUMN.ALL_VALUES.SUMMARY_TYPE.AVG:
                return AggregatorType.AVERAGE;
            case BIConfConstants.CONF.ADD_COLUMN.ALL_VALUES.SUMMARY_TYPE.MAX:
                return AggregatorType.MAX;
            case BIConfConstants.CONF.ADD_COLUMN.ALL_VALUES.SUMMARY_TYPE.MIN:
                return AggregatorType.MIN;
            default:
                return AggregatorType.COUNT;
        }
    }

    /**
     * nice job ! foundation
     * @param type target#getGroup()#getType()
     * @return
     */
    public static AggregatorType adaptorDashBoard(int type) {
        switch (type) {
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.SUM:
                return AggregatorType.SUM;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.AVG:
                return AggregatorType.AVERAGE;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.MAX:
                return AggregatorType.MAX;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.MIN:
                return AggregatorType.MIN;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.MIDDLE_VALUE:
                return AggregatorType.MEDIAN;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.VARIANCE:
                return AggregatorType.VARIANCE;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.STANDARD_DEVIATION:
                return AggregatorType.STANDARD_DEVIATION;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.DISTINCT_COUNT:
                return AggregatorType.DISTINCT;
            case BIDesignConstants.DESIGN.SUMMARY_TYPE.RECORD_COUNT:
                return AggregatorType.COUNT;
        }
        return AggregatorType.DUMMY;
    }

    /**
     * 合计方式
     *
     * @param type target#getMetric()
     * @return
     */
    public static AggregatorType adaptorMetric(int type) {
        switch (type) {
            case BIDesignConstants.DESIGN.METRIC_TYPE.SUM:
                return AggregatorType.SUM;
            case BIDesignConstants.DESIGN.METRIC_TYPE.AVERAGE:
                return AggregatorType.AVERAGE;
            case BIDesignConstants.DESIGN.METRIC_TYPE.MAX:
                return AggregatorType.MAX;
            case BIDesignConstants.DESIGN.METRIC_TYPE.MIN:
                return AggregatorType.MIN;
            case BIDesignConstants.DESIGN.METRIC_TYPE.MEDIAN:
                return AggregatorType.MEDIAN;
            case BIDesignConstants.DESIGN.METRIC_TYPE.VARIANCE:
                return AggregatorType.VARIANCE;
            case BIDesignConstants.DESIGN.METRIC_TYPE.STANDARD_DEVIATION:
                return AggregatorType.STANDARD_DEVIATION;
        }
        return AggregatorType.DUMMY;
    }

    public static AggregatorType adaptorRapidCal(int rapidType) {
        switch (rapidType) {
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.NONE:
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL:
                return AggregatorType.SUM;
        }
        return null;
    }

    /**
     * 计算指标这边只有四种聚合类型
     *
     * @param calTargetType
     * @return
     */
    public static AggregatorType adaptorCalTarget(int calTargetType) {
        switch (calTargetType) {
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MAX:
                return AggregatorType.MAX;
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MIN:
                return AggregatorType.MIN;
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_AVG:
                return AggregatorType.AVERAGE;
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_SUM:
                return AggregatorType.SUM;

        }
        return null;
    }
}
