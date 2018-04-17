package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/12/8.
 */
public abstract class AbstractSwiftNode<T extends SwiftNode> implements SwiftNode<T>, GroupByResultSet {
    protected T parent;
    protected T sibling;
    protected AggregatorValue[] aggregatorValues;

    public AbstractSwiftNode(int sumLength) {
        this.aggregatorValues = new AggregatorValue[sumLength];
    }

    protected AbstractSwiftNode() {}

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

    @Override
    public List<KeyValue<RowIndexKey, AggregatorValue[]>> getResultList() {
        return null;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return null;
    }

    @Override
    public int rowDimensionSize() {
        return 0;
    }
}
