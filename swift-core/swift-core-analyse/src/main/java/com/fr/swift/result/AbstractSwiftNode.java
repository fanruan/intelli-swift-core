package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.EmptyAggregatorValueRow;

/**
 * Created by pony on 2017/12/8.
 */
public abstract class AbstractSwiftNode implements SwiftNode {
    private static final long serialVersionUID = -1451247093003530849L;
    private SwiftNode parent;
    private SwiftNode sibling;
    private AggregatorValueSet aggregatorValues;


    AbstractSwiftNode() {
//        this.aggregatorValues = new AggregatorValue[0];
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
    public AggregatorValueRow asSingleAggRowValue() {
        if (null == aggregatorValues || aggregatorValues.isEmpty()) {
            return new EmptyAggregatorValueRow();
        }
        return aggregatorValues.iterator().next();
    }

    @Override
    public AggregatorValueSet getAggregatorValue() {
        return aggregatorValues;
    }

    @Override
    public void setAggregatorValue(AggregatorValueSet aggregatorValues) {
        this.aggregatorValues = aggregatorValues;
    }
}
