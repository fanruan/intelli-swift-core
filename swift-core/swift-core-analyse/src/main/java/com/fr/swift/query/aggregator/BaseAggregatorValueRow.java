package com.fr.swift.query.aggregator;

/**
 * @author yee
 * @date 2019-06-24
 */
public abstract class BaseAggregatorValueRow<T extends AggregatorValue> implements AggregatorValueRow<T> {

    private boolean valid;

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
