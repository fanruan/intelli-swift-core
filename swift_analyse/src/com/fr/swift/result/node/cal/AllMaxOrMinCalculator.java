package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.result.TargetGettingKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AllMaxOrMinCalculator extends AbstractTargetCalculator {

    private Comparator<Double> comparator;
    private Double value = null;

    public AllMaxOrMinCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex,
                                 Iterator<Number[]> iterator, boolean isMax) {
        super(paramIndex, resultIndex, iterator);
        this.comparator = isMax ? Comparators.<Double>asc() : Comparators.<Double>desc();
    }

    @Override
    public Object call() throws Exception {
        List<Number[]> rows = new ArrayList<Number[]>();
        while (iterator.hasNext()) {
            Number[] row = iterator.next();
            rows.add(row);
            Double v = row[paramIndex.getTargetIndex()].doubleValue();
            if (value == null) {
                value = v;
                continue;
            }
            if (comparator.compare(value, v) < 0) {
                value = v;
            }
        }
        for (Number[] row : rows) {
            row[resultIndex.getTargetIndex()] = value;
        }
        return null;
    }
}
