package com.fr.bi.common.persistent.writer;

import java.util.Iterator;

/**
 * This class created on 2016/6/6.
 *
 * @author Connery
 * @since 4.0
 */
public class IterableObj implements Iterable<Integer> {
    private Integer start = 0;
    private Integer max = 10;

    @Override
    public Iterator iterator() {
        return new InnerIterator(start);
    }

    private class InnerIterator implements Iterator<Integer> {
        Integer startNumber;

        public InnerIterator(Integer start) {
            this.startNumber = start;
        }

        @Override
        public boolean hasNext() {
            return startNumber < max;
        }

        @Override
        public Integer next() {
            return startNumber++;
        }

        @Override
        public void remove() {

        }
    }
}
