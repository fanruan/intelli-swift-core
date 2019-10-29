package com.fr.swift.result;

import com.fr.swift.util.Clearable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * node childs map
 *
 * @author Daniel
 */
public class ChildMap<T> implements Clearable, Iterable<T>, Serializable {

    private static final long serialVersionUID = -682636692617824298L;
    private LinkedHashMap<Object, Integer> lmp = new LinkedHashMap<Object, Integer>();
    private ArrayList<T> list = new ArrayList<T>();

    public int size() {
        return list.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        ChildMap map = (ChildMap) super.clone();
        map.lmp = (LinkedHashMap<Object, Integer>) this.lmp.clone();
        map.list = (ArrayList<T>) this.list.clone();
        return map;
    }


    public T get(Object key) {
        Integer index = lmp.get(key);
        if (index != null) {
            int i = index;
            if (i < size()) {
                return list.get(i);
            }
        }
        return null;
    }

    public T get(int index) {
        if (index < size()) {
            return list.get(index);
        }
        return null;
    }

    public T put(Object key, T value) {
        Integer i = lmp.put(key, list.size());
        list.add(value);
        return i == null ? null : list.get(i);
    }

    @Override
    public void clear() {
        lmp.clear();
        list.clear();
    }

    @Override
    public String toString() {
        return list.toString();
    }


    public int getIndex(Object key) {
        return lmp.get(key);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<T> getList() {
        return list;
    }
}