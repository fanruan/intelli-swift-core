/**
 *
 */
package com.fr.bi.stable.structure.array;

import com.fr.general.ComparatorUtils;

import java.util.Arrays;


public class ArrayKey<T> {

    private T[] array;

    public ArrayKey(T[] array) {
        this.array = array;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(array);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArrayKey other = (ArrayKey) obj;
        if (!ComparatorUtils.equals(array, other.array))
            return false;
        return true;
    }

    public T[] toArray() {
        return array;
    }

}