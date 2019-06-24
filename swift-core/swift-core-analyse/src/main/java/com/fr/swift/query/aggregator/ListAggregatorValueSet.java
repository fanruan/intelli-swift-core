package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
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

    public ListAggregatorValueSet() {
        this.rows = new ArrayList<AggregatorValueRow>();
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
    public void clear() {
        this.rows.clear();
        this.iterator = rows.iterator();
    }

    @Override
    public Iterator<Row> data() {
        return new MapperIterator<AggregatorValueRow, Row>(this, new Function<AggregatorValueRow, Row>() {
            @Override
            public Row apply(AggregatorValueRow param) {
                return param.data();
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return rows.isEmpty();
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
        iterator.remove();
    }
}
