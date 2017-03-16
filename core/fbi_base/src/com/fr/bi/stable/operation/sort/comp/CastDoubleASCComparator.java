package com.fr.bi.stable.operation.sort.comp;


/**
 * Created by roy on 16/11/1.
 */
public class CastDoubleASCComparator extends ASCComparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return super.compare(o1, o2);
        }
        Double o1Double = ((Number) o1).doubleValue();
        Double o2Double = ((Number) o2).doubleValue();
        return super.compare(o1Double, o2Double);
    }
}
