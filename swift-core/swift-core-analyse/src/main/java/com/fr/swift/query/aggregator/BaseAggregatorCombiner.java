package com.fr.swift.query.aggregator;

/**
 * @author yee
 * @date 2019-06-26
 */
public abstract class BaseAggregatorCombiner implements AggregatorValueCombiner {

    private boolean needCombine;

    private AggregatorValue[] aggregatorValues;

    public BaseAggregatorCombiner(int size) {
        this.aggregatorValues = new AggregatorValue[size];
    }

    @Override
    public boolean isNeedCombine() {
        return needCombine;
    }

    @Override
    public void setValue(int i, AggregatorValue value) {
        this.aggregatorValues[i] = value;
        this.needCombine |= value instanceof IterableAggregatorValue;
    }

    @Override
    public AggregatorValue[] getAggregatorValue() {
        return aggregatorValues;
    }


}
