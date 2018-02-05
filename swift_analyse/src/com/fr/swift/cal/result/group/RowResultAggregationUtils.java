package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.mapreduce.OutCollector;
import com.fr.swift.mapreduce.Reduce;
import com.fr.swift.mapreduce.Workflow;
import com.fr.swift.mapreduce.impl.MapCollector;
import com.fr.swift.mapreduce.impl.SequentialWorkflow;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.RowResultCollector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/1/2.
 */
public class RowResultAggregationUtils {

    public static RowResultCollector query(List<Query<RowResultCollector>> queries, List<Aggregator> aggregators,
                                           List<Sort> indexSorts) throws SQLException {
        // 这个地方对分块查询结果进行合并，依次进行结果合并、结果过滤、排序、维度分层汇总
        // 通过NodeFactory通过RowResultCollector构建Node
        List<InCollector<RowIndexKey, AggregatorValue[]>> rowResults = new ArrayList<InCollector<RowIndexKey, AggregatorValue[]>>();
        List<List<Map<Integer, Object>>> globalDictionaries = new ArrayList<List<Map<Integer, Object>>>();
        for (Query<RowResultCollector> query : queries) {
            RowResultCollector collector = query.getQueryResult();
            rowResults.add(collector.getRowResult());
            globalDictionaries.add(collector.getGlobalDictionaries());
        }
        InCollector<RowIndexKey, Iterable<AggregatorValue[]>> in = shuffle(rowResults);
        Workflow<RowIndexKey, AggregatorValue[]> workflow =
                new SequentialWorkflow<RowIndexKey, AggregatorValue[], RowIndexKey, AggregatorValue[]>(
                        new RowResultReducer(aggregators), in, new MapCollector<RowIndexKey, AggregatorValue[]>());
        // 这边聚合的行包括汇总行
        return new RowResultCollector(mergeDictionaries(globalDictionaries), workflow.run(), indexSorts);
    }

    /**
     * 合并字典
     *
     * @param globalDictionaries
     * @return
     */
    private static List<Map<Integer, Object>> mergeDictionaries(List<List<Map<Integer, Object>>> globalDictionaries) {
        List<Map<Integer, Object>> result = new ArrayList<Map<Integer, Object>>();
        for (int i = 0, size = globalDictionaries.get(0).size(); i < size; i++) {
            result.add(new HashMap<Integer, Object>());
        }
        for (List<Map<Integer, Object>> dict : globalDictionaries) {
            for (int i = 0; i < dict.size(); i++) {
                for (Map.Entry<Integer, Object> entry : dict.get(i).entrySet()) {
                    if (result.get(i).containsKey(entry.getKey())) {
                        continue;
                    }
                    result.get(i).put(entry.getKey(), entry.getValue());
                }
            }
        }
        return result;
    }

    private static class RowResultReducer implements Reduce<RowIndexKey, AggregatorValue[],
            RowIndexKey, AggregatorValue[]> {

        private List<Aggregator> aggregators;

        public RowResultReducer(List<Aggregator> aggregators) {
            this.aggregators = aggregators;
        }

        @Override
        public void reduce(RowIndexKey key, Iterator<AggregatorValue[]> in, OutCollector<RowIndexKey, AggregatorValue[]> out) {
            AggregatorValue[] result;
            assert in.hasNext();
            result = in.next();
            while (in.hasNext()) {
                AggregatorValue[] values = in.next();
                for (int i = 0; i < values.length; i++) {
                    aggregators.get(i).combine(result[i], values[i]);
                }
            }
            out.collect(new KeyValue<RowIndexKey, AggregatorValue[]>(key, result));
        }
    }

    /**
     * shuffle通常是洗牌打乱的意思，但是在mapreduce编程模式里面却是分类、排序、合并之类的意思（把多个mapper输出的结果重排了）
     *
     * @param rowResults
     * @return
     */
    private static InCollector<RowIndexKey, Iterable<AggregatorValue[]>> shuffle(List<InCollector<RowIndexKey, AggregatorValue[]>> rowResults) {
        final Map<RowIndexKey, List<AggregatorValue[]>> result = new HashMap<RowIndexKey, List<AggregatorValue[]>>();
        for (InCollector rowResult : rowResults) {
            Iterator<KeyValue<RowIndexKey, AggregatorValue[]>> iterator = rowResult.iterator();
            while (iterator.hasNext()) {
                KeyValue<RowIndexKey, AggregatorValue[]> keyValue = iterator.next();
                if (!result.containsKey(keyValue.getKey())) {
                    result.put(keyValue.getKey(), new ArrayList<AggregatorValue[]>());
                }
                result.get(keyValue.getKey()).add(keyValue.getValue());
            }
        }
        return new InCollector<RowIndexKey, Iterable<AggregatorValue[]>>() {
            @Override
            public Iterator<KeyValue<RowIndexKey, Iterable<AggregatorValue[]>>> iterator() {
                final Iterator<Map.Entry<RowIndexKey, List<AggregatorValue[]>>> iterator = result.entrySet().iterator();
                return new Iterator<KeyValue<RowIndexKey, Iterable<AggregatorValue[]>>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public KeyValue<RowIndexKey, Iterable<AggregatorValue[]>> next() {
                        Map.Entry<RowIndexKey, List<AggregatorValue[]>> entry = iterator.next();
                        return new KeyValue<RowIndexKey, Iterable<AggregatorValue[]>>(
                                entry.getKey(),
                                entry.getValue()
                        );
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }
}
