package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;

/**
 * Created by Handsome on 2018/2/24 0024 15:30
 */
public class MinCalculator extends AbstractAllDataCalculator {

    public static MinCalculator INSTANCE = new MinCalculator();


    @Override
    protected Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.MIN);
    }
}
