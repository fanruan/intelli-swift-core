package com.fr.bi.stable.operation.sort.comp;

/**
 * Created by roy on 16/11/2.
 */
public class CastFloatASCComparator extends ASCComparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return super.compare(o1, o2);
        }
        Float o1Float = ((Number) o1).floatValue();
        Float o2Float = ((Number) o2).floatValue();
        return super.compare(o1Float, o2Float);
    }
}
