package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueSet;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/4.
 */
abstract class AbstractTargetCalculator implements TargetCalculator {

    protected int paramIndex;
    protected int resultIndex;
    protected Iterator<Iterator<AggregatorValueSet>> iterators;

    public AbstractTargetCalculator(int paramIndex, int resultIndex, Iterator<Iterator<AggregatorValueSet>> iterators) {
        this.paramIndex = paramIndex;
        this.resultIndex = resultIndex;
        this.iterators = iterators;
    }
}
