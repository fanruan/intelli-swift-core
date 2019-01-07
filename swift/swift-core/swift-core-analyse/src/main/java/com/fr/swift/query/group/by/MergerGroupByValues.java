package com.fr.swift.query.group.by;

import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Comparator;
import java.util.List;

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
    protected Comparator<Pair<Object[], List<RowTraversal[]>>> getComparator() {
        return new Comparator<Pair<Object[], List<RowTraversal[]>>>() {
            @Override
            public int compare(Pair<Object[], List<RowTraversal[]>> o1, Pair<Object[], List<RowTraversal[]>> o2) {
                Object[] index1 = o1.getKey();
                Object[] index2 = o2.getKey();
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
