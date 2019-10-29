package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.AggregatorValue;
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
                          Iterator<Iterator<List<AggregatorValue[]>>> iterators, boolean asc) {
        super(paramIndex, resultIndex, iterators);
        this.asc = asc;
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<List<AggregatorValue[]>> iterator = iterators.next();
            List<Map<Double, Integer>> maps = null;
            List<List<AggregatorValue[]>> rows = new ArrayList<List<AggregatorValue[]>>();
            while (iterator.hasNext()) {
                List<AggregatorValue[]> row = iterator.next();
                rows.add(row);
                if (maps == null) {
                    maps = row.isEmpty() ? null : initMaps(row.size(), asc ? Comparators.<Double>asc() : Comparators.<Double>desc());
                }
                for (int i = 0; i < row.size(); i++) {
                    // TODO: 2018/5/2 空值问题处理
                    Double key = row.get(i)[paramIndex].calculate();
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
            for (List<AggregatorValue[]> row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    if (maps.get(i).isEmpty()) {
                        // 跳过没有值的情况
                        continue;
                    }
                    // 设置排名
                    row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(maps.get(i).get(row.get(i)[paramIndex].calculate()));
                }
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
