package com.fr.swift.cal.convert;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.SumAggregate;

/**
 * Created by pony on 2017/12/26.
 */
public class AggregatorFactory {
    public static Aggregator transformAggregator(){
        return SumAggregate.INSTANCE;
    }
}
