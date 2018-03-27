package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2018/3/26.
 */
public class AggregatorFactory {
    public static Aggregator createAggregator(AggregatorType type) {
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
            case STRING_COMBINE:
                return StringCombineAggregate.INSTANCE;
            default:
                return CountAggregator.INSTANCE;
        }
    }
}
