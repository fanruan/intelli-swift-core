package com.fr.swift.query.group.by;

import com.fr.swift.result.RowIndexKey;

import java.util.Comparator;

/**
 * Created by pony on 2018/3/29.
 */
public class MergerGroupByIndex extends MergerGroupBy<int[]> {
    //升序还是降序
    private boolean[] asc;

    public MergerGroupByIndex(MultiGroupByIndex[] iterators, boolean[] asc) {
        super(iterators);
        this.asc = asc;
        initMap();
    }


    @Override
    protected Comparator<RowIndexKey<int[]>> getComparator() {
        return new Comparator<RowIndexKey<int[]>>() {
            @Override
            public int compare(RowIndexKey<int[]> o1, RowIndexKey<int[]> o2) {
                int[] index1 = o1.getKey();
                int[] index2 = o2.getKey();
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
