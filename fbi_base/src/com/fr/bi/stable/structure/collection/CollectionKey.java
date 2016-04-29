/**
 *
 */
package com.fr.bi.stable.structure.collection;

import com.fr.bi.stable.utils.algorithem.BIHashCodeUtils;
import com.fr.general.ComparatorUtils;

import java.util.Collection;


public class CollectionKey<T> {

    private Collection<T> collection;

    public CollectionKey(Collection<T> collection) {
        this.collection = collection;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + BIHashCodeUtils.hashCode(collection);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CollectionKey other = (CollectionKey) obj;
        if (!ComparatorUtils.equals(collection, other.collection)) {
            return false;
        }
        return true;
    }

    public Collection<T> toCollection() {
        return collection;
    }

}