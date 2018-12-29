package com.fr.swift.converter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FindListImpl<T> implements FindList<T> {
    private List list;
    private Through<T> through;

    public FindListImpl(List list, Through<T> through) {
        this.list = list;
        this.through = through;
    }

    public FindListImpl(List list) {
        this.list = list;
    }

    @Override
    public List justForEach(final ConvertEach each) throws Exception {
        return new Through() {
            @Override
            public List go() throws Exception {
                for (int i = 0; i < list.size(); i++) {
                    each.forEach(i, list.get(i));
                }
                return new ArrayList(list);
            }
        }.go();
    }

    @Override
    public <S> List<T> forEach(final ConvertEach<S, T> each) throws Exception {
        return new Through<T>() {
            @Override
            public List<T> go() throws Exception {
                List<T> result = new ArrayList<T>();
                for (int i = 0; i < list.size(); i++) {
                    T bean = each.forEach(i, (S) list.get(i));
                    if (null != bean) {
                        result.add(bean);
                    }
                }
                return result;
            }
        }.go();
    }

    @Override
    public List<T> list() {
        try {
            if (null != through) {
                return through.go();
            } else {
                return forEach(SimpleEach.EMPTY);
            }
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public T get(int i) {
        if (i < 0 || i >= list.size()) {
            return null;
        }
        Object obj = list.get(i);
        if (obj instanceof ObjectConverter) {
            return ((ObjectConverter<T>) obj).convert();
        }
        return (T) obj;
    }
}
