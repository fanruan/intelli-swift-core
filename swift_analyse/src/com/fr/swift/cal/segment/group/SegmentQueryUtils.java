package com.fr.swift.cal.segment.group;

import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.mapreduce.Mapper;
import com.fr.swift.mapreduce.OutCollector;
import com.fr.swift.mapreduce.Workflow;
import com.fr.swift.mapreduce.impl.MapCollector;
import com.fr.swift.mapreduce.impl.SequentialWorkflow;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.multiby.MultiGroupBy;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.RowResultCollector;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/1/2.
 */
public class SegmentQueryUtils {

    public static RowResultCollector queryAll(List<Column> dimensions, List<Column> metrics,
                                              List<Aggregator> aggregators, DetailFilter filter) {
        // 这边先把RowIndex聚合，不做结果过滤、排序等。
        InCollector<RowIndexKey, RowTraversal> in = MultiGroupBy.groupByAll(dimensions, filter);
        // 开发调试使用线性工作流
        Workflow<RowIndexKey, AggregatorValue[]> workflow =
                new SequentialWorkflow<RowIndexKey, RowTraversal, RowIndexKey, AggregatorValue[]>(
                        new AggregationMapper(metrics, aggregators), in,
                        new MapCollector<RowIndexKey, AggregatorValue[]>());
        InCollector<RowIndexKey, AggregatorValue[]> aggregationResult = workflow.run();
        List<DictionaryEncodedColumn> dict = getDictionaryColumns(dimensions);
        // 合并当前行结果用到的分组值，key为全局字典序号
        List<Map<Integer, Object>> globalDictionaries = getGlobalDictionaries(aggregationResult, getDictionaryColumns(dimensions));
        // 这边把分块字典索引转为全局字典索引
        aggregationResult = new SequentialWorkflow<RowIndexKey, AggregatorValue[], RowIndexKey, AggregatorValue[]>(
                new GlobalIndexMapper(dict), aggregationResult,
                new MapCollector<RowIndexKey, AggregatorValue[]>()).run();
        return new RowResultCollector(globalDictionaries, aggregationResult);
    }

    private static List<Map<Integer, Object>> getGlobalDictionaries(InCollector<RowIndexKey, AggregatorValue[]> in, List<DictionaryEncodedColumn> dictionaries) {
        List<Map<Integer, Object>> result = new ArrayList<Map<Integer, Object>>();
        for (int i = 0; i < dictionaries.size(); i++) {
            result.add(new HashMap<Integer, Object>());
        }
        Iterator<KeyValue<RowIndexKey, AggregatorValue[]>> iterator = in.iterator();
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey, AggregatorValue[]> keyValue = iterator.next();
            int[] indexes = keyValue.getKey().getValues();
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] == -1) {
                    continue;
                }
                int globalIndex = dictionaries.get(i).getGlobalIndexByIndex(indexes[i]);
                if (result.get(i).containsKey(globalIndex)) {
                    continue;
                }
                result.get(i).put(globalIndex, dictionaries.get(i).getValue(indexes[i]));
            }
        }
        return result;
    }

    private static List<DictionaryEncodedColumn> getDictionaryColumns(List<Column> dimensions) {
        List<DictionaryEncodedColumn> dict = new ArrayList<DictionaryEncodedColumn>();
        for (Column column : dimensions) {
            dict.add(column.getDictionaryEncodedColumn());
        }
        return dict;
    }

    private static class GlobalIndexMapper implements Mapper<RowIndexKey, AggregatorValue[], RowIndexKey, AggregatorValue[]> {

        private List<DictionaryEncodedColumn> dict;

        public GlobalIndexMapper(List<DictionaryEncodedColumn> dict) {
            this.dict = dict;
        }

        @Override
        public void map(KeyValue<RowIndexKey, AggregatorValue[]> keyValue, OutCollector<RowIndexKey, AggregatorValue[]> outCollector) {
            int[] indexes = keyValue.getKey().getValues();
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] != -1) {
                    continue;
                }
                indexes[i] = dict.get(i).getGlobalIndexByIndex(indexes[i]);
            }
            outCollector.collect(keyValue);
        }
    }

    private static class AggregationMapper implements Mapper<RowIndexKey, RowTraversal,
            RowIndexKey, AggregatorValue[]> {

        private List<Column> metrics;
        private List<Aggregator> aggregators;

        public AggregationMapper(List<Column> metrics, List<Aggregator> aggregators) {
            this.metrics = metrics;
            this.aggregators = aggregators;
        }

        @Override
        public void map(KeyValue<RowIndexKey, RowTraversal> keyValue, OutCollector<RowIndexKey, AggregatorValue[]> outCollector) {
            AggregatorValue[] values = new AggregatorValue[metrics.size()];
            for (int i = 0; i < metrics.size(); i++) {
                // 如果指标比较多，这边也可以增加多线程计算
                values[i] = aggregators.get(i).aggregate(keyValue.getValue(), metrics.get(i));
            }
            outCollector.collect(new KeyValue<RowIndexKey, AggregatorValue[]>(keyValue.getKey(), values));
        }
    }
}
