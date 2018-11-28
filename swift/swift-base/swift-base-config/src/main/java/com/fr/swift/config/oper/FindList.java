package com.fr.swift.config.oper;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface FindList<T> {
    FindList EMPTY = new FindList() {
        @Override
        public List justForEach(Each each) {
            return Collections.emptyList();
        }

        @Override
        public List forEach(Each each) {
            return Collections.emptyList();
        }

        @Override
        public List list() {
            return Collections.emptyList();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object get(int i) {
            return null;
        }
    };

    List justForEach(Each each) throws Exception;

    List<T> forEach(Each<T> each) throws Exception;

    List<T> list();

    boolean isEmpty();

    int size();

    T get(int i);

    interface Each<T> {
        Each EMPTY = new Each() {
            @Override
            public void each(int idx, Object item) {

            }
        };

        void each(int idx, T item) throws Exception;
    }

    interface Through<T> {
        List<T> go() throws Exception;
    }
}
