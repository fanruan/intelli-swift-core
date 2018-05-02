package com.fr.swift.query.group.by;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.RowIndexKey;

import java.util.Comparator;

/**
 * Created by pony on 2018/3/29.
 */
public class MergerGroupByIndex extends MergerGroupBy<int[]> {

    public MergerGroupByIndex(MultiGroupByIndex[] iterators, boolean[] asc) {
        super(iterators, asc);
        init();
    }

    @Override
    protected <V> Comparator<KeyValue<RowIndexKey<int[]>, V>> getComparator() {
        return new Comparator<KeyValue<RowIndexKey<int[]>, V>>() {
            @Override
            public int compare(KeyValue<RowIndexKey<int[]>, V> o1, KeyValue<RowIndexKey<int[]>, V> o2) {
                int[] index1 = o1.getKey().getKey();
                int[] index2 = o2.getKey().getKey();
                for (int i = 0; i < index1.length; i++) {
                    int result = asc[i] ? index1[i] - index2[i] : index2[i] - index1[i];
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }
}
