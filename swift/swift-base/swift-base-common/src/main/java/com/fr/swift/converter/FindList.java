package com.fr.swift.converter;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface FindList<T> {
    FindList EMPTY = new FindList() {
        @Override
        public List justForEach(ConvertEach each) {
            return Collections.emptyList();
        }

        @Override
        public List forEach(ConvertEach each) {
            return Collections.emptyList();
        }

        @Override
        public List list() {
            return Collections.emptyList();
        }

        @Override
        public boolean isEmpty() {
            return true;
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

    List justForEach(ConvertEach each) throws Exception;

    <S> List<T> forEach(ConvertEach<S, T> each) throws Exception;

    List<T> list();

    boolean isEmpty();

    int size();

    T get(int i);

    interface ConvertEach<S, T> {
        T forEach(int idx, S item) throws Exception;
    }

    interface Through<T> {
        List<T> go() throws Exception;
    }

    abstract class SimpleEach<T> implements ConvertEach<Object, T> {

        public static SimpleEach EMPTY = new SimpleEach() {
            @Override
            protected void each(int idx, Object bean) throws Exception {
            }
        };

        @Override
        public T forEach(int idx, Object item) throws Exception {
            T bean = null;
            if (item instanceof ObjectConverter) {
                bean = ((ObjectConverter<T>) item).convert();
            } else {
                bean = (T) item;
            }
            each(idx, bean);
            return bean;
        }

        protected abstract void each(int idx, T bean) throws Exception;
    }
}

