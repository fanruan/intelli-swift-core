package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;

/**
 * Created by Handsome on 2018/2/24 0024 15:45
 */
public class MaxCalculator extends AbstractAllDataCalculator {

    public static MaxCalculator INSTANCE = new MaxCalculator();

    @Override
    protected Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.MAX);
    }
}
