package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.result.TargetGettingKey;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AllRankCalculator extends AbstractTargetCalculator {

    private Iterator<Number[]> iterator;
    private boolean asc;

    public AllRankCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex,
                             Iterator<Number[]> iterator, boolean asc) {
        super(paramIndex, resultIndex);
        this.iterator = iterator;
        this.asc = asc;
    }

    @Override
    public Object call() throws Exception {
        Map<Double, Integer> map = new TreeMap<Double, Integer>(asc ? Comparators.<Double>asc() : Comparators.<Double>desc());
        return null;
    }
}
