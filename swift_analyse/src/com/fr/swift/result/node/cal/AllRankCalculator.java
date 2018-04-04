package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.result.TargetGettingKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AllRankCalculator extends AbstractTargetCalculator {

    private boolean asc;

    public AllRankCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex,
                             Iterator<Number[]> iterator, boolean asc) {
        super(paramIndex, resultIndex, iterator);
        this.asc = asc;
    }

    @Override
    public Object call() throws Exception {
        Map<Double, Integer> map = new TreeMap<Double, Integer>(asc ? Comparators.<Double>asc() : Comparators.<Double>desc());
        List<Number[]> rows = new ArrayList<Number[]>();
        while (iterator.hasNext()) {
            Number[] row = iterator.next();
            rows.add(row);
            Double key = row[paramIndex.getTargetIndex()].doubleValue();
            Integer count = map.get(key);
            // 首先用map统计个数并排序
            if (count == null) {
                map.put(key, 1);
            } else {
                map.put(key, count + 1);
            }
        }
        Map.Entry<Double, Integer> lastEntry = null;
        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            // 算一下排名
            entry.setValue(lastEntry != null ? lastEntry.getValue() + entry.getValue() : entry.getValue());
            lastEntry = entry;
        }
        for (Number[] row : rows) {
            // 设置排名
            row[resultIndex.getTargetIndex()] = map.get(row[paramIndex.getTargetIndex()].doubleValue());
        }
        return null;
    }
}
