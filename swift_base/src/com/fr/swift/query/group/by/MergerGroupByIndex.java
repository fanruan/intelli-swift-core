package com.fr.swift.query.group.by;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.iterator.RowTraversal;

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
    protected Comparator<KeyValue<RowIndexKey<int[]>, RowTraversal[]>> getComparator() {
        return new Comparator<KeyValue<RowIndexKey<int[]>, RowTraversal[]>>() {
            @Override
            public int compare(KeyValue<RowIndexKey<int[]>, RowTraversal[]> o1,
                               KeyValue<RowIndexKey<int[]>, RowTraversal[]> o2) {
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
