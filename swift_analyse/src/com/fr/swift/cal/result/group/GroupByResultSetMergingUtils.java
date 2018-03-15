package com.fr.swift.cal.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.GroupByResultSetImpl;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupByResultSetMergingUtils {

    public static GroupByResultSet merge(List<GroupByResultSet> groupByResultSets,
                                         List<Aggregator> aggregators, List<Sort> indexSorts) {
        Map<RowIndexKey, KeyValue<RowIndexKey, AggregatorValue[]>> resultMap = new HashMap<RowIndexKey, KeyValue<RowIndexKey, AggregatorValue[]>>();
        List<Map<Integer, Object>> globalDictionaries = new ArrayList<Map<Integer, Object>>();
        for (GroupByResultSet resultSet : groupByResultSets) {
            addResultSet(resultSet.getRowResultIterator(), resultMap, aggregators);
            addDictionaries(resultSet.getGlobalDictionaries(), globalDictionaries);
        }
        return new GroupByResultSetImpl(resultMap.values().iterator(), globalDictionaries, indexSorts);
    }

    private static void addDictionaries(List<Map<Integer, Object>> dictionaries,
                                        List<Map<Integer, Object>> totalDictionaries) {
        if (totalDictionaries.size() == 0) {
            for (int i = 0; i < dictionaries.size(); i++) {
                totalDictionaries.add(new HashMap<Integer, Object>());
            }
        }
        for (int i = 0; i < dictionaries.size(); i++) {
            totalDictionaries.get(i).putAll(dictionaries.get(i));
        }
    }

    private static void addResultSet(Iterator<KeyValue<RowIndexKey, AggregatorValue[]>> iterator,
                                     Map<RowIndexKey, KeyValue<RowIndexKey, AggregatorValue[]>> map,
                                     List<Aggregator> aggregators) {
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey, AggregatorValue[]> keyValue = iterator.next();
            if (!map.containsKey(keyValue.getKey())) {
                map.put(keyValue.getKey(), keyValue);
            } else {
                AggregatorValue[] result = map.get(keyValue.getKey()).getValue();
                AggregatorValue[] values = keyValue.getValue();
                for (int i = 0; i < values.length; i++) {
                    // 这边合并两个值，并把合并后的值设置给result[i]
                    aggregators.get(i).combine(result[i], values[i]);
                }
            }
        }
    }
}
