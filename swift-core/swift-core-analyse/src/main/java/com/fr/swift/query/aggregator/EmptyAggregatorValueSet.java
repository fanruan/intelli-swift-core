package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author yee
 * @date 2019-06-25
 */
public class EmptyAggregatorValueSet implements AggregatorValueSet {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public Iterator<Row> data() {
        return Collections.<Row>emptyList().iterator();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Iterator<AggregatorValueRow> iterator() {
        return IteratorUtils.emptyIterator();
    }
}
