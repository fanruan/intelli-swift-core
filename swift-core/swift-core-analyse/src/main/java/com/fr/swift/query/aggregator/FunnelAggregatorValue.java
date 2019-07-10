package com.fr.swift.query.aggregator;

import com.fr.swift.query.aggregator.funnel.FunnelNodeAggregatorValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelAggregatorValue implements IterableAggregatorValue<SwiftNodeAggregatorValue> {
    private Map<FunnelGroupKey, FunnelAggValue> valueMap;

    public FunnelAggregatorValue(Map<FunnelGroupKey, FunnelAggValue> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public SwiftNodeAggregatorValue calculateValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() {
        return new FunnelAggregatorValue(new HashMap<FunnelGroupKey, FunnelAggValue>(valueMap));
    }


    @Override
    public Iterator<SwiftNodeAggregatorValue> iterator() {
        Iterator<Map.Entry<FunnelGroupKey, FunnelAggValue>> iterator = valueMap.entrySet().iterator();
        return new MapperIterator<Map.Entry<FunnelGroupKey, FunnelAggValue>, SwiftNodeAggregatorValue>(iterator, new Function<Map.Entry<FunnelGroupKey, FunnelAggValue>, SwiftNodeAggregatorValue>() {
            @Override
            public SwiftNodeAggregatorValue apply(Map.Entry<FunnelGroupKey, FunnelAggValue> p) {
                return new FunnelNodeAggregatorValue(p.getKey(), p.getValue());
            }
        });
    }
}
