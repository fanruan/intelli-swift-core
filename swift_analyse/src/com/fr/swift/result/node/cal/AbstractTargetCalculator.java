package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
abstract class AbstractTargetCalculator implements TargetCalculator {

    protected int paramIndex;
    protected int resultIndex;
    protected Iterator<List<AggregatorValue[]>> iterator;

    public AbstractTargetCalculator(int paramIndex, int resultIndex, Iterator<List<AggregatorValue[]>> iterator) {
        this.paramIndex = paramIndex;
        this.resultIndex = resultIndex;
        this.iterator = iterator;
    }
}
