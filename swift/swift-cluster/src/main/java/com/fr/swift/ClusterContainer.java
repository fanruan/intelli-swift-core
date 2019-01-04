package com.fr.swift;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class ClusterContainer<T> {

    protected Map<String, T> container = new ConcurrentHashMap<String, T>();

    protected void add(final String id, final T t) {
        container.put(id, t);
    }

    protected void remove(final String id) {
        container.remove(id);
    }

    protected T get(final String id) {
        return container.get(id);
    }

    protected List<T> getAllUseable() {
        List<T> list = new ArrayList<T>();
        list.addAll(container.values());
        return list;
    }

    protected void removeAll() {
        container.clear();
    }

    protected boolean contains(final String id) {
        return container.containsKey(id);
    }
}
