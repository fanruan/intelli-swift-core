package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lyon on 2018/4/4.
 */
public class RankCalculator extends AbstractTargetCalculator {

    private boolean asc;

    public RankCalculator(int paramIndex, int resultIndex,
                          Iterator<Iterator<AggregatorValueSet>> iterators, boolean asc) {
        super(paramIndex, resultIndex, iterators);
        this.asc = asc;
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<AggregatorValueSet> iterator = iterators.next();
            List<Map<Double, Integer>> maps = null;
            List<AggregatorValueSet> rows = new ArrayList<AggregatorValueSet>();
            while (iterator.hasNext()) {
                AggregatorValueSet row = iterator.next();
                rows.add(row);
                if (maps == null) {
                    maps = row.isEmpty() ? null : initMaps(row.size(), asc ? Comparators.<Double>asc() : Comparators.<Double>desc());
                }
                int i = 0;
                while (row.hasNext()) {
                    // TODO: 2018/5/2 空值问题处理
                    Double key = row.next().getValue(paramIndex).calculate();
                    // 跳过空值
                    if (Double.isNaN(key)) {
                        continue;
                    }
                    Integer count = maps.get(i).get(key);
                    // 首先用map统计个数并排序
                    if (count == null) {
                        maps.get(i).put(key, 1);
                    } else {
                        maps.get(i).put(key, count + 1);
                    }
                }
                row.reset();
            }
            int rank = 1;
            for (Map<Double, Integer> map : maps) {
                for (Map.Entry<Double, Integer> entry : map.entrySet()) {
                    // 算一下排名
                    int value = entry.getValue();
                    entry.setValue(rank);
                    rank += value;
                }
            }
            for (AggregatorValueSet row : rows) {
                int i = 0;
                while (row.hasNext()) {
                    if (maps.get(i++).isEmpty()) {
                        // 跳过没有值的情况
                        continue;
                    }
                    // 设置排名
                    AggregatorValueRow next = row.next();
                    next.setValue(resultIndex, new DoubleAmountAggregatorValue(maps.get(i).get(next.getValue(paramIndex).calculate())));
                }
                row.reset();
            }
        }
        return null;
    }

    private static List<Map<Double, Integer>> initMaps(int size, Comparator<Double> comparator) {
        List<Map<Double, Integer>> maps = new ArrayList<Map<Double, Integer>>();
        for (int i = 0; i < size; i++) {
            maps.add(new TreeMap<Double, Integer>(comparator));
        }
        return maps;
    }
}
