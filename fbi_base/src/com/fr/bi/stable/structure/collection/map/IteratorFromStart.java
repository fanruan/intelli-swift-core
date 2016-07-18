package com.fr.bi.stable.structure.collection.map;

import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/10/12.
 */
public class IteratorFromStart implements Iterator<Map.Entry> {

    private Iterator<Map.Entry> iter;

    private boolean first = true;

    private transient Map.Entry entry;

    public IteratorFromStart(Iterator<Map.Entry> iter, Object key) {
        this.iter = iter;
        init(key);
    }

    private void init(Object key){
        if (iter != null){
            while (iter.hasNext()){
                Map.Entry entry = iter.next();
                if (ComparatorUtils.equals(entry.getKey(), key)){
                    this.entry = entry;
                    break;
                }
            }
        }
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        if (iter != null){
            return iter.hasNext() || (entry != null && first);
        }
        return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     */
    @Override
    public Map.Entry next() {
        if (first){
            first = false;
            return entry;
        } else {
            return iter.next();
        }
    }

    /**
     * Removes from the underlying collection the last element returned
     * by this iterator (optional operation).  This method can be called
     * only once per call to {@link #next}.  The behavior of an iterator
     * is unspecified if the underlying collection is modified while the
     * iteration is in progress in any way other than by calling this
     * method.
     *
     * @throws UnsupportedOperationException if the {@code remove}
     *                                       operation is not supported by this iterator
     * @throws IllegalStateException         if the {@code next} method has not
     *                                       yet been called, or the {@code remove} method has already
     *                                       been called after the last call to the {@code next}
     *                                       method
     * @implSpec The default implementation throws an instance of
     * {@link UnsupportedOperationException} and performs no other action.
     */
    @Override
    public void remove() {

    }
}