package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.query.aggregator.funnel.FunnelNodeAggregatorValue;
import com.fr.swift.cloud.query.group.FunnelGroupKey;
import com.fr.swift.cloud.structure.iterator.MapperIterator;
import com.fr.swift.cloud.util.function.Function;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelAggregatorValue implements IterableAggregatorValue<SwiftNodeAggregatorValue> {
    private Map<FunnelGroupKey, FunnelHelperValue> valueMap;

    public FunnelAggregatorValue(Map<FunnelGroupKey, FunnelHelperValue> valueMap) {
        this.valueMap = new TreeMap<FunnelGroupKey, FunnelHelperValue>(new Comparator<FunnelGroupKey>() {
            @Override
            public int compare(FunnelGroupKey o1, FunnelGroupKey o2) {
                return o1.compareTo(o2);
            }
        });
        this.valueMap.putAll(valueMap);
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public SwiftNodeAggregatorValue calculateValue() {
        return iterator().next();
    }

    @Override
    public Object clone() {
        return new FunnelAggregatorValue(new HashMap<FunnelGroupKey, FunnelHelperValue>(valueMap));
    }


    @Override
    public Iterator<SwiftNodeAggregatorValue> iterator() {

        Iterator<Map.Entry<FunnelGroupKey, FunnelHelperValue>> iterator = valueMap.entrySet().iterator();
        return new MapperIterator<Map.Entry<FunnelGroupKey, FunnelHelperValue>, SwiftNodeAggregatorValue>(iterator, new Function<Map.Entry<FunnelGroupKey, FunnelHelperValue>, SwiftNodeAggregatorValue>() {
            @Override
            public SwiftNodeAggregatorValue apply(Map.Entry<FunnelGroupKey, FunnelHelperValue> p) {
                return new FunnelNodeAggregatorValue(p.getKey(), p.getValue());
            }
        });
    }

    public Map<FunnelGroupKey, FunnelHelperValue> getValueMap() {
        return valueMap;
    }

    public void combine(FunnelAggregatorValue value) {
        for (Map.Entry<FunnelGroupKey, FunnelHelperValue> entry : value.getValueMap().entrySet()) {
            FunnelHelperValue contestAggValue = valueMap.get(entry.getKey());
            if (contestAggValue == null) {
                valueMap.put(entry.getKey(), new FunnelHelperValue(entry.getValue().getCount(), entry.getValue().getPeriods()));
                continue;
            }
            int[] values = entry.getValue().getCount();
            int[] counters = contestAggValue.getCount();
            for (int i = 0; i < values.length; i++) {
                counters[i] += values[i];
            }
            List<List<Long>> valuePeriods = entry.getValue().getPeriods();
            List<List<Long>> lists = contestAggValue.getPeriods();
            // TODO: 2018/9/25 可以做好一部分排序，以及使用基本类型
            for (int i = 0; i < valuePeriods.size(); i++) {
                for (int j = 0; j < valuePeriods.get(i).size(); j++) {
                    lists.get(i).add(valuePeriods.get(i).get(j));
                }
            }
        }
    }
}
