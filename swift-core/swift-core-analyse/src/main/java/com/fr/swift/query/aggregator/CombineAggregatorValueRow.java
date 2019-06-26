package com.fr.swift.query.aggregator;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;

import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2019-06-25
 */
public class CombineAggregatorValueRow implements AggregatorValueRow<AggregatorValue> {
    private AggregatorValueRow current;
    private AggregatorValueRow other;
    private List<Aggregator> aggregators;
    private Function<Pair<Aggregator, List<AggregatorValue>>, AggregatorValue> fn;
    private AggregatorValueRow combineRow;

    public CombineAggregatorValueRow(AggregatorValueRow current,
                                     AggregatorValueRow other,
                                     List<Aggregator> aggregators,
                                     Function<Pair<Aggregator, List<AggregatorValue>>, AggregatorValue> fn) {
        this.current = current;
        this.other = other;
        this.aggregators = aggregators;
        this.fn = fn;
    }

    @Override
    public void setValue(int i, AggregatorValue value) {
        initCombineRow();
        this.combineRow.setValue(i, value);
    }

    @Override
    public AggregatorValue getValue(int i) {
        initCombineRow();
        return this.combineRow.getValue(i);
    }

    @Override
    public List<Object> data() {
        initCombineRow();
        return combineRow.data();
    }

    @Override
    public int getSize() {
        initCombineRow();
        return combineRow.getSize();
    }

    private void initCombineRow() {
        if (null == combineRow) {
            AggregatorValue[] values = new AggregatorValue[current.getSize()];
            for (int i = 0; i < this.aggregators.size(); i++) {
                values[i] = fn.apply(Pair.of(aggregators.get(i), Arrays.asList(current.getValue(i), other.getValue(i))));
            }
            this.combineRow = new ListAggregatorValueRow(values);
        }
    }
}
