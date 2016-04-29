package com.fr.bi.stable.operation.sort.comp;

public class ASCComparableComparator<T extends Comparable<T>> extends AbstractComparator<T> {

    @Override
    public int compare(T o1, T o2) {
    	if(o1 == o2){
    		return 0;
    	}
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }

}