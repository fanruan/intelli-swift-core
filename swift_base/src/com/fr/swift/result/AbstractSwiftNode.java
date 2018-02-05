package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/8.
 */
public abstract class AbstractSwiftNode<T extends SwiftNode> implements SwiftNode<T>, SwiftResultSet {
    protected T parent;
    protected T sibling;
    protected AggregatorValue[] aggregatorValues;

    public AbstractSwiftNode(int sumLength) {
        this.aggregatorValues = new AggregatorValue[sumLength];
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
    public void setAggregatorValue(TargetGettingKey key, AggregatorValue value) {
        aggregatorValues[key.getTargetIndex()] = value;
    }

    @Override
    public AggregatorValue getAggregatorValue(TargetGettingKey key) {
        return aggregatorValues[key.getTargetIndex()];
    }

    @Override
    public AggregatorValue[] getAggregatorValue() {
        return aggregatorValues;
    }

    @Override
    public void setAggregatorValue(AggregatorValue[] aggregatorValues) {
        this.aggregatorValues = aggregatorValues;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }
}
