package com.fr.swift.query.group.by;

import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2018/3/29.
 */
public class MergerGroupByIndex extends MergerGroupBy<int[]> {

    public MergerGroupByIndex(MultiGroupByIndex[] iterators, boolean[] asc) {
        super(iterators, asc);
        init();
    }

    @Override
    protected Comparator<Pair<int[], List<RowTraversal[]>>> getComparator() {
        return new Comparator<Pair<int[], List<RowTraversal[]>>>() {
            @Override
            public int compare(Pair<int[], List<RowTraversal[]>> o1, Pair<int[], List<RowTraversal[]>> o2) {
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
