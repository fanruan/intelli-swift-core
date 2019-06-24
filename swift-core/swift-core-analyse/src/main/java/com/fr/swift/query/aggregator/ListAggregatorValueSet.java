package com.fr.swift.query.aggregator;

import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2019-06-24
 */
public class ListAggregatorValueSet implements AggregatorValueSet {

    private List<AggregatorValueRow> rows;
    private Iterator<AggregatorValueRow> iterator;

    public ListAggregatorValueSet(List<AggregatorValueRow> rows) {
        this.rows = rows;
        this.reset();
    }

    @Override
    public void reset() {
        this.iterator = this.rows.iterator();
    }

    @Override
    public int size() {
        return rows.size();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public AggregatorValueRow next() {
        return iterator.next();
    }

    @Override
    public void remove() {

    }
}
