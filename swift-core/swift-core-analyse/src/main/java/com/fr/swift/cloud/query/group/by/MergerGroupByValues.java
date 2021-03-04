package com.fr.swift.cloud.query.group.by;

import java.util.Comparator;

/**
 * Created by pony on 2018/3/29.
 */
public class MergerGroupByValues extends MergerGroupBy<Object[]> {
    //原始的comparators, bool[] asc决定是否按原始comparator升序
    private final Comparator[] comparators;

    public MergerGroupByValues(MultiGroupByValues[] iterators, Comparator[] comparators, boolean[] asc) {
        super(iterators, asc);
        this.comparators = comparators;
        init();
    }

    @Override
    protected Comparator<Item<Object[]>> getComparator() {
        return new Comparator<Item<Object[]>>() {
            @Override
            public int compare(Item<Object[]> o1, Item<Object[]> o2) {
                Object[] index1 = o1.getPair().getKey();
                Object[] index2 = o2.getPair().getKey();
                for (int i = 0; i < index1.length; i++) {
                    int result = asc[i] ? comparators[i].compare(index1[i], index2[i]) : comparators[i].compare(index2[i], index1[i]);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }
}
