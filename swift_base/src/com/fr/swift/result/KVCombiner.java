package com.fr.swift.result;

import com.fr.swift.query.aggregator.Combiner;

import java.util.List;

/**
 * Created by Lyon on 2018/3/30.
 */
public class KVCombiner<V> implements Combiner<KeyValue<RowIndexKey<int[]>, V[]>> {

    private List<? extends Combiner<V>> combiners;

    public KVCombiner(List<? extends Combiner<V>> combiners) {
        this.combiners = combiners;
    }

    @Override
    public void combine(KeyValue<RowIndexKey<int[]>, V[]> current, KeyValue<RowIndexKey<int[]>, V[]> other) {
        V[] values = current.getValue();
        V[] otherValues = other.getValue();
        for (int i = 0; i < combiners.size(); i++) {
            combiners.get(i).combine(values[i], otherValues[i]);
        }
    }
}
