package com.fr.swift.result.node.cal;

import com.fr.swift.result.TargetGettingKey;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/4.
 */
public class SumOfAboveCalculator extends AbstractTargetCalculator {

    private Iterator<Number[]> iterator;

    public SumOfAboveCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex, Iterator<Number[]> iterator) {
        super(paramIndex, resultIndex);
        this.iterator = iterator;
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
