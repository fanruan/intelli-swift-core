package com.fr.swift.query.group.by;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;

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
    protected <V> Comparator<KeyValue<RowIndexKey<Object[]>, V>> getComparator() {
        return new Comparator<KeyValue<RowIndexKey<Object[]>, V>>() {
            @Override
            public int compare(KeyValue<RowIndexKey<Object[]>, V> o1, KeyValue<RowIndexKey<Object[]>, V> o2) {
                Object[] index1 = o1.getKey().getKey();
                Object[] index2 = o2.getKey().getKey();
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
