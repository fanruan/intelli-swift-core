package com.fr.swift.result.node.cal;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/4.
 */
abstract class AbstractTargetCalculator implements TargetCalculator {

    protected int paramIndex;
    protected int resultIndex;
    protected Iterator<Number[]> iterator;

    public AbstractTargetCalculator(int paramIndex, int resultIndex, Iterator<Number[]> iterator) {
        this.paramIndex = paramIndex;
        this.resultIndex = resultIndex;
        this.iterator = iterator;
    }
}
