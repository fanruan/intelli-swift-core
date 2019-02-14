package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;

/**
 * Created by pony on 2017/12/8.
 */
public abstract class AbstractSwiftNode<T extends SwiftNode> implements SwiftNode<T> {
    private static final long serialVersionUID = -1451247093003530849L;
    protected T parent;
    protected T sibling;
    protected AggregatorValue[] aggregatorValues;

    public AbstractSwiftNode(int sumLength) {
        this.aggregatorValues = new AggregatorValue[sumLength];
    }

    protected AbstractSwiftNode() {
        this.aggregatorValues = new AggregatorValue[0];
    }

    @Override
    public T getSibling() {
        return sibling;
    }

    @Override
    public void setSibling(T sibling) {
        this.sibling = sibling;
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public void setAggregatorValue(int key, AggregatorValue value) {
        aggregatorValues[key] = value;
    }

    @Override
    public AggregatorValue getAggregatorValue(int key) {
        return aggregatorValues[key];
    }

    @Override
    public AggregatorValue[] getAggregatorValue() {
        return aggregatorValues;
    }

    @Override
    public void setAggregatorValue(AggregatorValue[] aggregatorValues) {
        this.aggregatorValues = aggregatorValues;
    }
}
