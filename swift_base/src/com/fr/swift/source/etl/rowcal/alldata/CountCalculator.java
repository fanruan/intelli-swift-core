package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;

/**
 * Created by Handsome on 2018/2/24 0024 15:43
 */
public class CountCalculator extends AbstractAllDataCalculator {

    public static CountCalculator INSTANCE = new CountCalculator();

    @Override
    protected Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.COUNT);
    }
}
