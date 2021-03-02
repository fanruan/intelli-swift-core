package com.fr.swift.cloud.query.group.by;

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
    protected Comparator<Item<int[]>> getComparator() {
        return new Comparator<Item<int[]>>() {
            @Override
            public int compare(Item<int[]> o1, Item<int[]> o2) {
                int[] index1 = o1.getPair().getKey();
                int[] index2 = o2.getPair().getKey();
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
