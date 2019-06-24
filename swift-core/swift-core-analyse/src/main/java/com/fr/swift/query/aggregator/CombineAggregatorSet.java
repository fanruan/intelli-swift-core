package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2019-06-25
 */
public class CombineAggregatorSet implements AggregatorValueSet {

    private AggregatorValueSet current;
    private AggregatorValueSet other;
    private List<Aggregator> aggregators;
    private Function<Pair<Aggregator, List<AggregatorValue>>, AggregatorValue> fn;

    public CombineAggregatorSet(AggregatorValueSet current,
                                AggregatorValueSet other,
                                List<Aggregator> aggregators,
                                Function<Pair<Aggregator, List<AggregatorValue>>, AggregatorValue> fn) {
        this.current = current;
        this.other = other;
        this.aggregators = aggregators;
        this.fn = fn;
    }

    @Override
    public void reset() {
        current.reset();
        other.reset();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        current.clear();
        other.clear();
    }

    @Override
    public Iterator<Row> data() {
        return new MapperIterator<AggregatorValueRow, Row>(this, new Function<AggregatorValueRow, Row>() {
            @Override
            public Row apply(AggregatorValueRow p) {
                return p.data();
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return current.isEmpty();
    }

    @Override
    public boolean hasNext() {
        return current.hasNext();
    }

    @Override
    public AggregatorValueRow next() {
        return new CombineAggregatorValueRow(current.next(), other.next(), aggregators, fn);
    }

    @Override
    public void remove() {

    }
}
