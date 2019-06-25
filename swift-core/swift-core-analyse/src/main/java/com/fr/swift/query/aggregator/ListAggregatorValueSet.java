package com.fr.swift.query.aggregator;

import com.fr.swift.source.ListBasedRow;
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

    public ListAggregatorValueSet(List<AggregatorValueRow> rows) {
        this.rows = rows;
    }

    public ListAggregatorValueSet() {
        this.rows = new ArrayList<AggregatorValueRow>();
    }

    @Override
    public int size() {
        return rows.size();
    }

    @Override
    public void clear() {
        this.rows.clear();

    }

    @Override
    public Iterator<Row> data() {
        return new MapperIterator<AggregatorValueRow, Row>(iterator(), new Function<AggregatorValueRow, Row>() {
            @Override
            public Row apply(AggregatorValueRow param) {
                return new ListBasedRow(param.data());
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return rows.isEmpty();
    }


    @Override
    public Iterator<AggregatorValueRow> iterator() {
        return rows.iterator();
    }
}
