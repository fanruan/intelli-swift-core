package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.query.aggregator.funnel.FunnelPathKey;
import com.fr.swift.cloud.query.aggregator.funnel.FunnelPathsNodeAggregatorValue;
import com.fr.swift.cloud.structure.iterator.MapperIterator;
import com.fr.swift.cloud.util.function.Function;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yee
 * @date 2019-07-12
 */
public class FunnelPathsAggregatorValue implements IterableAggregatorValue<FunnelPathsNodeAggregatorValue> {

    private Map<FunnelPathKey, FunnelAggregatorValue> valueMap;

    public FunnelPathsAggregatorValue(Map<FunnelPathKey, FunnelAggregatorValue> valueMap) {
        this.valueMap = new TreeMap<FunnelPathKey, FunnelAggregatorValue>(new Comparator<FunnelPathKey>() {
            @Override
            public int compare(FunnelPathKey o1, FunnelPathKey o2) {
                return o1.compareTo(o2);
            }
        });
        this.valueMap.putAll(valueMap);
    }

    @Override
    public double calculate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FunnelPathsNodeAggregatorValue calculateValue() {
        return iterator().next();
    }

    @Override
    public Object clone() {
        return new FunnelPathsAggregatorValue(new HashMap<FunnelPathKey, FunnelAggregatorValue>(valueMap));
    }

    @Override
    public Iterator<FunnelPathsNodeAggregatorValue> iterator() {
        return new MapperIterator<Map.Entry<FunnelPathKey, FunnelAggregatorValue>, FunnelPathsNodeAggregatorValue>(valueMap.entrySet().iterator(),
                new Function<Map.Entry<FunnelPathKey, FunnelAggregatorValue>, FunnelPathsNodeAggregatorValue>() {
                    @Override
                    public FunnelPathsNodeAggregatorValue apply(final Map.Entry<FunnelPathKey, FunnelAggregatorValue> p) {
                        return new FunnelPathsNodeAggregatorValue(p.getKey(), p.getValue());
                    }
                });
    }

    public void combine(FunnelPathsAggregatorValue value) {
        for (Map.Entry<FunnelPathKey, FunnelAggregatorValue> entry : value.valueMap.entrySet()) {
            FunnelAggregatorValue contestAggValue = valueMap.get(entry.getKey());
            if (contestAggValue == null) {
                valueMap.put(entry.getKey(), (FunnelAggregatorValue) contestAggValue.clone());
                continue;
            }
            contestAggValue.combine(entry.getValue());
        }
    }
}
