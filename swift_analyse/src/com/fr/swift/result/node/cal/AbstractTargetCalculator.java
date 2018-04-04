package com.fr.swift.result.node.cal;

import com.fr.swift.result.TargetGettingKey;

import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Created by Lyon on 2018/4/4.
 */
abstract class AbstractTargetCalculator implements Callable<Object> {

    protected TargetGettingKey paramIndex;
    protected TargetGettingKey resultIndex;
    protected Iterator<Number[]> iterator;

    public AbstractTargetCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex, Iterator<Number[]> iterator) {
        this.paramIndex = paramIndex;
        this.resultIndex = resultIndex;
    }
}
