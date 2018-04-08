package com.fr.swift.result.node.cal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AllAverageCalculator extends AbstractTargetCalculator {

    public AllAverageCalculator(int paramIndex, int resultIndex, Iterator<Number[]> iterator) {
        super(paramIndex, resultIndex, iterator);
    }

    @Override
    public Object call() throws Exception {
        List<Number[]> rows = new ArrayList<Number[]>();
        Double sum = .0;
        while (iterator.hasNext()) {
            Number[] row = iterator.next();
            rows.add(row);
            sum += row[paramIndex].doubleValue();
        }
        Double average = rows.isEmpty() ? .0 : sum / rows.size();
        for (Number[] row : rows) {
            row[resultIndex] = average;
        }
        return null;
    }
}
