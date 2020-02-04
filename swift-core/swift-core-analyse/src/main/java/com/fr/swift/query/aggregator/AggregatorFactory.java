package com.fr.swift.query.aggregator;

import com.fr.swift.query.aggregator.extension.DistinctDateYMD;
import com.fr.swift.query.aggregator.extension.LimitRowAggregator;
import com.fr.swift.query.aggregator.extension.TopPercentileAggregator;
import com.fr.swift.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;

/**
 * Created by pony on 2018/3/26.
 */
public class AggregatorFactory {
    public static Aggregator createAggregator(AggregatorType type, Object... params) {
        switch (type) {
            case SUM:
                return SumAggregate.INSTANCE;
            case MAX:
                return MaxAggregate.INSTANCE;
            case MIN:
                return MinAggregate.INSTANCE;
            case AVERAGE:
                return AverageAggregate.INSTANCE;
            case DISTINCT:
                return DistinctAggregate.INSTANCE;
            case HLL_DISTINCT:
                return HLLDistinctAggregator.INSTANCE;
            case STRING_COMBINE:
                return StringCombineAggregate.INSTANCE;
            case SINGLE_VALUE:
                return SingleObjectAggregate.INSTANCE;
            case DATE_MAX:
                return DateMaxAggregate.INSTANCE;
            case DATE_MIN:
                return DateMinAggregate.INSTANCE;
            case MEDIAN:
                return MedianAggregate.INSTANCE;
            case VARIANCE:
                return VarianceAggregate.INSTANCE;
            case STANDARD_DEVIATION:
                return StandardDeviationAggregator.INSTANCE;
            case COUNT:
                return CountAggregator.INSTANCE;

            // extension
            case LIMIT_ROW:
                if (params != null && params.length == 1) {
                    int limitRow = Integer.parseInt(params[0].toString());
                    return new LimitRowAggregator(limitRow);
                }
            case DISTINCT_DATE_YMD:
                return DistinctDateYMD.INSTANCE;
            case TOP_PERCENTILE: {
                if (params != null && params.length == 2) {
                    double percentile = Double.parseDouble(params[0].toString());
                    int numberOfSignificantValueDigits = Integer.parseInt(params[1].toString());
                    return new TopPercentileAggregator(percentile, numberOfSignificantValueDigits);
                }
            }
            case FUNNEL:
                if (null != params) {
                    return new FunnelAggregator((FunnelAggregationBean) params[0]);
                }
                return DummyAggregator.INSTANCE;
            case FUNNEL_PATHS:
                if (null != params) {
                    return new FunnelPathsAggregator((FunnelPathsAggregationBean) params[0]);
                }
                return DummyAggregator.INSTANCE;
            default:
                return DummyAggregator.INSTANCE;
        }
    }
}
