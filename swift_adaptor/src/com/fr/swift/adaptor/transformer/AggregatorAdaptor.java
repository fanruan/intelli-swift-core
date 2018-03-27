package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.source.etl.utils.ETLConstant;

/**
 * Created by pony on 2017/12/26.
 */
public class AggregatorAdaptor {
    public static Aggregator transformAggregator(int fieldType, int aggregatorType) {
        return AggregatorFactory.createAggregator(getAggregatorType(fieldType, aggregatorType));
    }

    private static AggregatorType getAggregatorType(int fieldType, int aggregatorType) {
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
            case ETLConstant.CONF.GROUP.STRING.COUNT:
                return AggregatorType.DISTINCT;
            case ETLConstant.CONF.GROUP.STRING.APPEND:
                return AggregatorType.STRING_COMBINE;
            default:
                return AggregatorType.COUNT;
        }
    }

    private static AggregatorType getDateAggregatorType(int aggregatorType) {
        switch (aggregatorType) {
            case ETLConstant.CONF.GROUP.DATE.COUNT:
                return AggregatorType.DISTINCT;
            case ETLConstant.CONF.GROUP.DATE.ET:
                return AggregatorType.MIN;
            case ETLConstant.CONF.GROUP.DATE.LT:
                return AggregatorType.MAX;
            default:
                return AggregatorType.COUNT;
        }
    }

    private static AggregatorType getNumberAggregatorType(int aggregatorType) {
        switch (aggregatorType) {
            case ETLConstant.CONF.GROUP.NUMBER.SUM:
                return AggregatorType.SUM;
            case ETLConstant.CONF.GROUP.NUMBER.AVG:
                return AggregatorType.AVERAGE;
            case ETLConstant.CONF.GROUP.NUMBER.MEDIAN:
            case ETLConstant.CONF.GROUP.NUMBER.MAX:
                return AggregatorType.MAX;
            case ETLConstant.CONF.GROUP.NUMBER.MIN:
                return AggregatorType.MIN;
            case ETLConstant.CONF.GROUP.NUMBER.STANDARD_DEVIATION:
            case ETLConstant.CONF.GROUP.NUMBER.VARIANCE:
            case ETLConstant.CONF.GROUP.NUMBER.COUNT:
                return AggregatorType.DISTINCT;
            default:
                return AggregatorType.COUNT;
        }
    }
}
