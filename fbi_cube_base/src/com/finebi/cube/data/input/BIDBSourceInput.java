package com.finebi.cube.data.input;

import java.util.Iterator;

/**
 * Created by Connery on 2016/3/1.
 */
public class BIDBSourceInput {
    public class BIDBSourceReader implements Iterator {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public void remove() {

        }
    }
}
