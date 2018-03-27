package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;

/**
 * Created by Handsome on 2018/2/24 0024 15:33
 */
public class AvgCalculator extends AbstractAllDataCalculator {

    public static AvgCalculator INSTANCE = new AvgCalculator();


    @Override
    protected Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.AVERAGE);
    }
}
