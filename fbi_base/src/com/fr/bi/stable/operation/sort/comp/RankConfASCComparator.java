package com.fr.bi.stable.operation.sort.comp;

/**
 * Created by kary on 17-1-17.
 */
public class RankConfASCComparator extends ASCComparator{
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return super.compare(o1, o2);
    }
}
