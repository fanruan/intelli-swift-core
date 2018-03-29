package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIConfConstants;
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
            case BIConfConstants.CONF.GROUP.NUMBER.MAX:
                return AggregatorType.MAX;
            case BIConfConstants.CONF.GROUP.NUMBER.MIN:
                return AggregatorType.MIN;
            case BIConfConstants.CONF.GROUP.NUMBER.STANDARD_DEVIATION:
            case BIConfConstants.CONF.GROUP.NUMBER.VARIANCE:
            case BIConfConstants.CONF.GROUP.NUMBER.COUNT:
                return AggregatorType.DISTINCT;
            default:
                return AggregatorType.COUNT;
        }
    }

    /**
     * nice job foundation
     * @param fieldType
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
}
