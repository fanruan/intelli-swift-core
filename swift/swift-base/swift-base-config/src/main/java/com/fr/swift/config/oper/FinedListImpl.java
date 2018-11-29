package com.fr.swift.config.oper;


import com.fr.swift.config.bean.ObjectConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FinedListImpl<T> implements FindList<T> {
    private List list;
    private Through<T> through;

    public FinedListImpl(List list, Through<T> through) {
        this.list = list;
        this.through = through;
    }

    public FinedListImpl(List list) {
        this.list = list;
    }

    @Override
    public List justForEach(final Each each) throws Exception {
        return new Through() {
            @Override
            public List go() throws Exception {
                for (int i = 0; i < list.size(); i++) {
                    each.each(i, list.get(i));
                }
                return new ArrayList(list);
            }
        }.go();
    }

    @Override
    public List<T> forEach(final Each<T> each) throws Exception {
        return new Through<T>() {
            @Override
            public List go() throws Exception {
                List<T> result = new ArrayList<T>();
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    T bean = null;
                    if (obj instanceof ObjectConverter) {
                        bean = ((ObjectConverter<T>) obj).convert();
                    } else {
                        bean = (T) obj;
                    }
                    each.each(i, bean);
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
                return forEach(Each.EMPTY);
            }
        } catch (Exception e) {
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
        Object obj = list.get(i);
        if (obj instanceof ObjectConverter) {
            return ((ObjectConverter<T>) obj).convert();
        }
        return (T) obj;
    }
}
