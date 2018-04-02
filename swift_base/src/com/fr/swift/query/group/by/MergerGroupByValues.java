package com.fr.swift.query.group.by;

import com.fr.swift.result.RowIndexKey;

import java.util.Comparator;

/**
 * Created by pony on 2018/3/29.
 */
public class MergerGroupByValues extends MergerGroupBy<Object> {
    //原始的comparators
    private final Comparator[] comparators;
    //是否按原始comparator升序
    private final boolean[] asc;

    public MergerGroupByValues(MultiGroupByValues[] iterators, Comparator[] comparators, boolean[] asc) {
        super(iterators);
        this.comparators = comparators;
        this.asc = asc;
        initMap();
    }

    @Override
    protected Comparator<RowIndexKey<Object>> getComparator() {
        return new Comparator<RowIndexKey<Object>>() {
            @Override
            public int compare(RowIndexKey<Object> o1, RowIndexKey<Object> o2) {
                Object[] index1 = (Object[]) o1.getKey();
                Object[] index2 = (Object[]) o2.getKey();
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
