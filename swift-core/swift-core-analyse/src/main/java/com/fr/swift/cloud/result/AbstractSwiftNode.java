package com.fr.swift.cloud.result;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;

/**
 * Created by pony on 2017/12/8.
 */
public abstract class AbstractSwiftNode implements SwiftNode {
    private static final long serialVersionUID = -1451247093003530849L;
    private SwiftNode parent;
    private SwiftNode sibling;
    private AggregatorValue[] aggregatorValues;

    public AbstractSwiftNode(int sumLength) {
        this.aggregatorValues = new AggregatorValue[sumLength];
    }

    AbstractSwiftNode() {
        this.aggregatorValues = new AggregatorValue[0];
    }

    @Override
    public SwiftNode getSibling() {
        return sibling;
    }

    @Override
    public void setSibling(SwiftNode sibling) {
        this.sibling = sibling;
    }

    @Override
    public SwiftNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SwiftNode parent) {
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
