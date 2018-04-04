package com.fr.swift.result.node.cal;

import com.fr.swift.result.TargetGettingKey;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AllSumOfAboveCalculator extends AbstractTargetCalculator {

    public AllSumOfAboveCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex, Iterator<Number[]> iterator) {
        super(paramIndex, resultIndex, iterator);
    }

    @Override
    public Object call() throws Exception {
        Number[] lastRow = null;
        while (iterator.hasNext()) {
            Double lastSum = lastRow == null ? 0 : lastRow[resultIndex.getTargetIndex()].doubleValue();
            Number[] row = iterator.next();
            row[resultIndex.getTargetIndex()] = lastSum + row[paramIndex.getTargetIndex()].doubleValue();
            lastRow = row;
        }
        return null;
    }
}
